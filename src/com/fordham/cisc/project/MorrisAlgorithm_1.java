package com.fordham.cisc.project;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;

public class MorrisAlgorithm_1 {

	static Logger LOGGER = Logger.getLogger("GenEvents");
	//static final String fileName = "events.txt";
	static Random random =new Random();
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		// Validate the Input Arguments
		validateInputArgs(args);
		String fileName = args[0];
		double epsilon = Double.valueOf( args[1] );
		double delta = Double.valueOf( args[2] );
		double reportTime = Double.valueOf( args[3] );
			
		int noOfCopy = getNoOfCopiesToRun(epsilon, delta);

		Stream<String> fileStream = readFile( fileName );
		
		List<String> eventList = fileStream.collect(Collectors.toList());
		double timer = 0;
		int predictedN = 0;
		int predictedN_Morris = 0;
		int newPredictedN = 0;
		int predictedCount_Morris = 0;
		int predictedCount_MorrisP = 0;
		int predictedCount_MorrisPP = 0;
		
		for(int actualN=0; actualN < eventList.size(); actualN ++){
			// System.out.println("######################## Counter == "+actualN);
			timer += Double.valueOf(eventList.get(actualN));
			
			newPredictedN = morris(predictedN, actualN);
			predictedCount_Morris = ((int) Math.pow(2, newPredictedN)) - 1;
			
			predictedCount_MorrisP = morrisPlus(predictedN, actualN, noOfCopy);
			
			predictedCount_MorrisPP = morrisPlusPlus(predictedN, actualN, epsilon, delta);
			
			// Finally Update the predictedN for next run.
			predictedN = newPredictedN;
			
			// Outputing the result at the report time, setting up the timer back to "0" to get the next report time.
			if(timer >= reportTime){
				System.out.println("Output result Actual   = "+ actualN );
				System.out.println("Output result Morris   = "+ predictedCount_Morris );
				System.out.println("Output result Morris+  = "+ predictedCount_MorrisP );
				System.out.println("Output result Morris++ = "+ predictedCount_MorrisPP );
				System.out.println(" ************************************************** ");
				timer = 0;
			}
		}
		
		// System.out.println("********* " + timer);
		System.out.println("Output result Actual   = "+ eventList.size() );
		System.out.println("Output result Morris   = "+ predictedCount_Morris );
		System.out.println("Output result Morris+  = "+ predictedCount_MorrisP );
		System.out.println("Output result Morris++ = "+ predictedCount_MorrisPP );
		System.out.println(" ************************************************** ");
		
		closeStream( fileStream );
	}
	
	
	/**
	 * 
	 * @param predictedN
	 * @param actualN
	 * @param epsilon
	 * @param delta
	 * @return The predicted median count from T instance of morris++  
	 */
	public static int morrisPlusPlus(int predictedN, int actualN, double epsilon, double delta){
		
		// This is number of instance to run Morris+
		double eachInstanceDelta = 1 / 3.0;
		int noOfInstance = (int) Math.ceil( Math.log( (1 / delta )));
		
		// This is for the number of copies for morris+ to run morris algo, 
		// The error probability in this case is 1/3 for each t instance.
		int noOfCopy = getNoOfCopiesToRun(epsilon, eachInstanceDelta);
		
		List<Integer> morrisPlusInstanceList = new ArrayList<Integer>();
		for(int instance=0; instance < noOfInstance; instance++){
			morrisPlusInstanceList.add( morrisPlus(predictedN, actualN, noOfCopy) );
		}
		
		// Find the median of the MorrisPlus Output for all instance.
		Collections.sort( morrisPlusInstanceList );
		int median = 0;
		int sizeOfList = morrisPlusInstanceList.size();
		if( sizeOfList % 2 == 0)
			median = (morrisPlusInstanceList.get( (sizeOfList/2) ) + morrisPlusInstanceList.get( ((sizeOfList/2) -1 )) / 2);
		else 
			median = morrisPlusInstanceList.get( (sizeOfList/2) );
		
		return median;
	}

	/**
	 * 
	 * @param predictedN
	 * @param actualN
	 * @param epsilon
	 * @param delta
	 * @return The predicted Count by Morris+ algorithm
	 */
	public static int morrisPlus(int predictedN, int actualN, int noOfCopy){
		
		// int noOfCopy = (int) Math.ceil( 1 / (2 * (Math.pow(epsilon, 2.0) * delta)));
		
		int predictedCount_MorrisP = 0;
		double tmpCount = 0;
		int tmpN = predictedN;
		for(int copy=0; copy < noOfCopy; copy++){
			tmpN = morris(tmpN, actualN);
			//System.out.println("&&&&&&& tmpN " + tmpN);
			tmpCount += ((int) Math.pow(2, tmpN)) - 1;
			//System.out.println("&&&&&&& tmpCount " + tmpCount);
		}
		predictedCount_MorrisP = (int) tmpCount / noOfCopy;
		
		return predictedCount_MorrisP;
	}
	
	/**
	 * This method implements the morris algorithm 
	 * @param predictedN
	 * @return the updated predictedN counter
	 */
	public static int morris(int predictedN, int actualN){
		
		// updating the predicted N for the first iteration
		if (actualN == 0)
			predictedN += 1;
		// end

		// Getting 1/2^x probability to update the predicted counter
		// boolean probibility = random.nextInt((int) Math.pow(2, predictedN)) == 0;
		// Another way of getting the probability
		boolean probibility = ThreadLocalRandom.current().nextInt( (int) Math.pow(2, predictedN) ) == 0;
		//System.out.println("********** Probability == " + probibility);
		// update the predicted counter with the probability (1/2^x)
		if (probibility)
			predictedN += 1;
		
		return predictedN;
	}
	
	public static int getNoOfCopiesToRun(double epsilon, double delta) {
		
		// This is "S" independent copy for Morris+	:- S > (1 / 2^(e^2)*delta) = O(1/delta)
		int noOfCopy = (int) Math.ceil( 1 / (2 * (Math.pow(epsilon, 2.0) * delta)));
		// int noOfCopy = (int) Math.ceil( 1 / ((Math.pow(epsilon, 2.0) * delta)));
		// System.out.println(noOfCopy);
		
		return noOfCopy;
	}
	
	public static Stream<String> readFile( String fileName ){
		
		Stream<String> stream = null;
		try {
			stream = Files.lines(Paths.get(fileName));
	        /*stream.forEach( (string) -> {
	        	System.out.println("== " + string);
	        });*/
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return stream;
	}
	
	public static void validateInputArgs(String[] args){
		
		
		if(!(args.length == 4) ){
			errorMessage("Please pass the required argument to the program, in the following order - (fileName ,epsilpon, delta , How_Often) ", true, 0);
		}
		
		String fileName = "";
		try {
			fileName = args[0];
			Path filePath = Paths.get(fileName);
			boolean isFileExist = Files.exists( filePath );
			if(!isFileExist){
				errorMessage("File doesn't exist", true, 0);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			errorMessage("Error Occored while accessing the file", true, null);
			e.printStackTrace();
			// Program terminating message
			errorMessage(null, true, 0);
		}
		
		double epsilon = 0;
		try {
			epsilon = Double.valueOf(args[1]);
			if(!(0 < epsilon && epsilon <= 1.0)){
				errorMessage("Epsilon (Allowable Error)  has to be a floating point number greater than 0 and less than 1", true, 0);
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			errorMessage("Epsilon (Allowable Error) has to be a floating point number greater than 0 and less than 1", true, 0);
			// e.printStackTrace();
			// Program terminating message
			//errorMessage("", true, 0);
		}
		
		double delta = 0;
		try {
			delta = Double.valueOf(args[2]);
			if(!(0 < delta && delta <= 1.0)){
				errorMessage("Delta (Failure Probability)  has to be a floating point number greater than 0 and less than 1", true, 0);
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			errorMessage("Delta (Failure Probability) has to be a floating point number greater than 0 and less than 1", true, 0);
			// e.printStackTrace();
			// Program terminating message
			//errorMessage("", true, 0);
		}
		
		double reportTime = 0;
		try {
			reportTime = Double.valueOf(args[3]);
			if(0 >= reportTime){
				errorMessage("Report time (How_Often)  has to be a positve number", true, 0);
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			errorMessage("Report time (Hoe_Often)  has to be a positve number", true, 0);
			// e.printStackTrace();
			// Program terminating message
			//errorMessage("", true, 0);
		}
		
		
	}
	
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

	public static void closeStream(Stream<String> stream){
		
		stream.close();
	}

}
