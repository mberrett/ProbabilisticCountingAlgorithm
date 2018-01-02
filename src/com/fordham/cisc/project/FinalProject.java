package com.fordham.cisc.project;

/**********************************************************************
* CISC-5835                    : Big Data Algorithm
* Final Programming Assignment : Approximate counting using Morris++ Algorithm
* Project Team Size 		   : 4 Person
* Project Team (4) 			   : Vaibhav Dixit <vdixit@fordham.edu>, Matias Berretta Magarinos <mberrettamagarinos@fordham.edu>
* 				      			 Rohini Mandge <rmandge@fordham.edu>, Kwami Nyaku <kwami.nyaku@gmail.com>
* Submission Date 			   : 12 December 2017
*
* This program implements the Morris++ algorithm for approximate counting.
* time.
*
* This program takes the following input argument 
* 1 Name of the event file    - Name of the event file
* 2 Allowable error (Epsilon) - Has to be a floating point number between 0 and 1 (Excluding 0 & 1)
* 3 Error Probability (Delta) - Has to be a floating point number between 0 and 1 (Excluding 0 & 1)
* 4 Counting Report Time      - Has to be a positive number
* 
* In order to implement Morris++ algorithm, we have implemented Morris+ and Morris algorithms.
* Input arguments are being used to calculate S(independent copy of Morris algorithm for Morris+)
* and T instance ( instance of Morris+ for Morris++)
* 
* Details about Morris++ Algorithm is defined at http://www.dsm.fordham.edu/~agw/big-data-alg/handouts/bd01.pdf
* Details about assignment is at http://www.dsm.fordham.edu/~agw/big-data-alg/hw/project-description.pdf
* 
* Author: Vaibhav Dixit <vdixit@fordham.edu>
* 
* 
* Date: 12 December 2017
*
**********************************************************************/

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class FinalProject {

	// Logger instance to log the error message. 
	// Appropriate log level is been used for the error messages. (Warning, Info, Error)
	static Logger LOGGER = Logger.getLogger("GenEvents");
    // Random class object to generate the random number for required probability	
	static Random random =new Random();
	
	// Number of Instance of Morris+ for Morris++
	static int noOfMorrisPlusInstance;
	// Number of Independent Copies of Morris for Morris+ Also
	static int noOfMorrisCopy;
	// This static list will have the size of equal to noOfMorrisCopy and hold the updated counter for each copy
	static List<Integer> morrisPCopies;
	// This static list will have the size equal to T instance of morris+ and will contain a list of updated counter for each Morris+ instance
	static List< List<Integer> > morrisPPInstance;
	
	// Decimal Formatter
	static DecimalFormat df = new DecimalFormat("#.00"); 
	
	/**
	 * This is the main method of the program.
	 * 
	 * @param args - Array of Input arguments
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		long startTime = System.currentTimeMillis();   // Start time of the program
		
		// Validate the Input Arguments
		validateInputArgs(args);
		
		// START :- create program variables from input argument
		String fileName = args[0];
		double epsilon = Double.valueOf( args[1] );     // Allowable Error
		double delta = Double.valueOf( args[2] );	    // Failure probability
		double reportTime = Double.valueOf( args[3] );  // How_oftem time to output the predicted count.
		// END :- create program variables from input argument
		
		// Init Algorithm Param ( S, T and initiate the arraylist to hold the updated counter for S independent copies)
		initAlgoParams(epsilon, delta);

		Stream<String> fileStream = readFile( fileName );                   // String stream of the given event file
		List<String> eventList = fileStream.collect(Collectors.toList());   // collect the event stream to a list to loop it through
		
		double timer = 0;                  // timer variable to determine when to output based on [How_Often] input time
		double tmpTimer = 0;               // Temp variable to show the output time in the console output.
		int predictedN = 0;                // Predicted Counter (X) from Morris Algorithm
		int predictedCount_Morris = 0;     // Predicted Count from Morris Algorithm
		int predictedCount_MorrisP = 0;    // Predicted Count from Morris+ Algorithm
		int predictedCount_MorrisPP = 0;   // Predicted Count from Morris++ Algorithm
		
		// Output Table Header , Switch the print line if you want to see the output from all three versions of algo (Morris, Morris+, Morris++)
		System.out.format("%15s%20s%20s\n", "OutputTime" , "Actual count", "Morris++ Count");
		//System.out.format("%12s%23s%20s%20s%20s\n", "OutputTime" , "Actual count","Morris Count", "Morris+ Count", "Morris++ Count");
		
		for(int actualN=0; actualN < eventList.size(); actualN ++){
			// System.out.println("######################## Counter == "+actualN);
			timer += Double.valueOf(eventList.get(actualN));
			// This is just to show in the output
			tmpTimer += Double.valueOf(eventList.get(actualN));
			
			// START :- uncomment the following statements if you want to see the output from all three versions of algo (Morris, Morris+, Morris++)
			//predictedN = morris(predictedN, actualN);
			//predictedCount_Morris = ((int) Math.pow(2, predictedN)) - 1;
			
			//predictedCount_MorrisP = morrisPlus(actualN, morrisPCopies );
			// END :- uncomment the following statements if you want to see the output from all three versions of algo (Morris, Morris+, Morris++)
			
			predictedCount_MorrisPP = morrisPlusPlus(actualN, epsilon, delta);
			
			// Outputing the result at the report time, setting up the timer back to "0" to get the next report time.
			if(timer >= reportTime){
				// Output Table Row , Switch the print line if you want to see the output from all three versions of algo (Morris, Morris+, Morris++)
				System.out.format("%10s%20s%20s\n", df.format(tmpTimer) , actualN, predictedCount_MorrisPP);
				//System.out.format("%10s%20s%20s%20s%20s\n", df.format(tmpTimer) , actualN, predictedCount_Morris, predictedCount_MorrisP, predictedCount_MorrisPP);
				// Resetting the timer back to 0 to get the next report time.
				timer = 0;
			}
		}
	
		// Output the predicted count for the remaining events (Outside the loop)
		// Output Table Row , Switch the print line if you want to see the output from all three versions of algo (Morris, Morris+, Morris++)
		System.out.format("%10s%20s%20s\n", df.format(tmpTimer) , eventList.size(), predictedCount_MorrisPP);
		//System.out.format("%10s%20s%20s%20s%20s\n", df.format(tmpTimer) , eventList.size(), predictedCount_Morris, predictedCount_MorrisP, predictedCount_MorrisPP);
		
		// Closing the event stream manually to avoid memory overflow error.
		closeStream( fileStream );
		
		long endTime = System.currentTimeMillis();
		System.out.println("\n Elapse Time (In seconds) :- " + ((endTime - startTime)/1000.0));
	} 

	/**
	 * This method implements the Morris++ Algorithm. It executes the T instance of Morris+, each with the failure probability of 1/3 (0.33)
	 * 
	 * @param actualN - Actual Counter
	 * @param epsilon - Allowable Error
	 * @param delta   - Failure Probability
	 * 
	 * @return Median predicted count of all T instance of Morris++.
	 */
	public static int morrisPlusPlus(int actualN, double epsilon, double delta){
		
		List<Integer> morrisPlusInstanceList = new ArrayList<Integer>();     // List to add the output of Morris+ for each T instance.
		for(int instance=0; instance < noOfMorrisPlusInstance; instance++){
			//System.out.println("******** instance "+instance);
			
			morrisPlusInstanceList.add( morrisPlus( actualN, morrisPPInstance.get( instance ) ) );
		}
		
		// Find the median of the MorrisPlus Output for all instance.
		Collections.sort( morrisPlusInstanceList );
		// System.out.println(morrisPlusInstanceList.toString());
		int median = 0;
		int sizeOfList = morrisPlusInstanceList.size();
		if( sizeOfList % 2 == 0)
			median = (morrisPlusInstanceList.get( (sizeOfList/2) ) + morrisPlusInstanceList.get( ((sizeOfList/2) -1 )) / 2);
		else 
			median = morrisPlusInstanceList.get( (sizeOfList/2) );
		
		return median;
	}

	/**
	 * This method implements the Morris+ algorithm. I runs the Morris algorithm for S independent copies. 
	 * 
	 * @param actualN - Actual counter 
	 * @param morrisPCopies - List containing the predictedCount for each independent copy (Index of the list represent the individual copy)
	 * 
	 * @return The average predicted count of S independent runs of Morris Algo.
	 */
	public static int morrisPlus(int actualN, List<Integer> morrisPCopies){
		
		// int noOfCopy = (int) Math.ceil( 1 / (2 * (Math.pow(epsilon, 2.0) * delta)));
		int predictedCount_MorrisP = 0;   // Predicted count from Morris+ algo
		double tmpCount = 0;              // Temp variable used to count all copies of morris's predicted counts , To take the average
		int tmpN;                         // Predicted counter (X) for each independent copy of Morris
		for(int copy=0; copy < noOfMorrisCopy; copy++){
			tmpN = morrisPCopies.get( copy );
			tmpN = morris(tmpN, actualN);
			//System.out.println("&&&&&&& tmpN " + tmpN);
			// add the updated counter in the list.
			morrisPCopies.set( copy, tmpN );
			tmpCount += ((int) Math.pow(2, tmpN)) - 1;
			//System.out.println("&&&&&&& tmpCount " + tmpCount);
		}
		predictedCount_MorrisP =  (int) Math.round( tmpCount / noOfMorrisCopy ) ;

		return predictedCount_MorrisP;
	}
	
	/**
	 * Implementation of Morris algorithm, This method returns to updated predicted counter (X) with probability (1/2^X)
	 * 
	 * @param predictedN
	 * @param actualN
	 * 
	 * @return updated counter (X)
	 */
	public static int morris(int predictedN, int actualN){
		
		// updating the predicted N for the first iteration
		if (actualN == 0)
			predictedN += 1;
		// end

		// Getting 1/2^x probability to update the predicted counter
		// This function Returns a pseudorandom, uniformly distributed int value between 0 (inclusive) and the specified value (exclusive), 
		// drawn from this random number generator's sequence.
		boolean probibility = random.nextInt((int) Math.pow(2, predictedN)) == 0;
		// Another way of getting the probability
		// boolean probibility = ThreadLocalRandom.current().nextInt( (int) Math.pow(2, predictedN) ) == 0;
		//System.out.println("********** Probability == " + probibility);
		// update the predicted counter with the probability (1/2^x)
		if (probibility)
			predictedN += 1;
		
		return predictedN;
	}
	
	/**
	 * This methos initialize the algorithm params for Morris+ & Morris++
	 * and initialize a arraylist of size "S", for updated predicted counter.
	 * Also initialize a Arraylist of size T,for each instance of Morris+ run.
	 * 
	 * @param epsilon - Allowable Error
	 * @param delta   - Failure Probability
	 */
	private static void initAlgoParams(double epsilon, double delta) {
		// TODO Auto-generated method stub
		
		noOfMorrisPlusInstance = (int) Math.ceil( Math.log( (1 / delta )));
		// For Morris++, each morris+ instance runs with error probability as 1/3
		double errorProbForEachMorrisInst = 0.33;       // or Morris++, each morris+ instance runs with error probability as 1/3
		noOfMorrisCopy = getNoOfCopiesToRun(epsilon, errorProbForEachMorrisInst);
		//noOfMorrisCopy = getNoOfCopiesToRun(epsilon, delta);
		
		// Initial predicted counter for morris is 0, so insert 0 for all morris independent copy
		morrisPCopies = new ArrayList<Integer>();
		for(int copy=0; copy < noOfMorrisCopy; copy ++)
			morrisPCopies.add(0);
		
		morrisPPInstance = new ArrayList< List<Integer> >();
		for(int instance=0; instance < noOfMorrisPlusInstance; instance++ ) {
			List<Integer> morrisPCopiesTmp = new ArrayList<Integer>();
			for(int copy=0; copy < noOfMorrisCopy; copy ++)
				morrisPCopiesTmp.add(0);
			
			morrisPPInstance.add( morrisPCopiesTmp );
		}		
	}
	
	/**
	 * Utility method to get the value of S independent copies of Morris for Morris+ algorithm
	 * S > (1 / 2^(e^2)*delta) = Theta(1/delta)
	 * 
	 * @param epsilon - Allowable Error
	 * @param delta   - Failure probability
	 * @return value of S
	 */
	public static int getNoOfCopiesToRun(double epsilon, double delta) {
		
		// This is "S" independent copy for Morris+	:- S > (1 / 2^(e^2)*delta) = Theta(1/delta)
		int noOfCopy = (int) Math.ceil( 1 / (2 * (Math.pow(epsilon, 2.0) * delta)));
		// int noOfCopy = (int) Math.ceil( 1 / ((Math.pow(epsilon, 2.0) * delta)));
		// int noOfCopy = (int) Math.ceil( 1 / delta);
		
		return noOfCopy;
	}
	
	/**
	 * This method is to validate the user input.
	 * 
	 * @param args - Program input argument
	 */
	public static void validateInputArgs(String[] args){
		
		if(!(args.length == 4) ){
			errorMessage("Please pass the required argument to the program, in the following order - (fileName ,epsilpon, delta , How_Often) ", true, 0);
		}
		
		// Validate the event file
		String fileName = "";
		try {
			fileName = args[0];
			Path filePath = Paths.get(fileName);
			boolean isFileExist = Files.exists( filePath );
			if(!isFileExist){
				errorMessage("Event File doesn't exist", true, 0);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			errorMessage("Error Occored while accessing the event file", true, 0);
			// Program terminating message
		}
		
		// Validate the epsilon value
		double epsilon = 0;
		try {
			epsilon = Double.valueOf(args[1]);
			if(!(0 < epsilon && epsilon <= 1.0)){
				errorMessage("Epsilon (Allowable Error)  has to be a floating point number greater than 0 and less than 1", true, 0);
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			errorMessage("Epsilon (Allowable Error) has to be a floating point number greater than 0 and less than 1", true, 0);
		}
		
		// Validate the delta value
		double delta = 0;
		try {
			delta = Double.valueOf(args[2]);
			if(!(0 < delta && delta <= 1.0)){
				errorMessage("Delta (Failure Probability)  has to be a floating point number greater than 0 and less than 1", true, 0);
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			errorMessage("Delta (Failure Probability) has to be a floating point number greater than 0 and less than 1", true, 0);
		}
		
		// Validate the report time (How_Often)
		double reportTime = 0;
		try {
			reportTime = Double.valueOf(args[3]);
			if(0 >= reportTime){
				errorMessage("Report time (How_Often)  has to be a positive number", true, 0);
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			errorMessage("Report time (Hoe_Often)  has to be a positive number", true, 0);
		}
		
		
	}
	
	/**
	 * Utility method to print the console message with appropriate log level. Also, terminate the program in case of a severe error.
	 * 
	 * @param msg - String message to print on console
	 * @param isError - Log Level
	 * @param exitCode - System exit code in case of termination
	 */
	public static void errorMessage(String msg, boolean isError, Integer exitCode) {

		if (msg != null && !(msg.equals(""))) {
			if (isError)
				LOGGER.log(Level.SEVERE, msg);
			else
				LOGGER.log(Level.WARNING, msg);
		}
		if (exitCode != null) {
			LOGGER.log(Level.SEVERE, "******* Terminating program ******* ");
			System.exit(0);
		}
	}

	/**
	 * Read the event file and create a string stream.
	 * 
	 * @param fileName
	 * @return Stream<String> String stream of event file
	 */
	public static Stream<String> readFile( String fileName ){
		
		Stream<String> stream = null;
		try {
			stream = Files.lines(Paths.get(fileName));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			errorMessage("Error Occored while accessing the event file", true, 0);
		}
		return stream;
	}
	
	/**
	 * This method is to close the event stream manually to avoid the memory overflow error.
	 * 
	 * @param stream - Stream to close
	 * @throws Exception
	 */
	public static void closeStream(Stream<String> stream){
		
		stream.close();
	}

}
