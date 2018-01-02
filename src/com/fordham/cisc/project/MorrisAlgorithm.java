package com.fordham.cisc.project;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MorrisAlgorithm {

	static Logger LOGGER = Logger.getLogger("GenEvents");
	static final String fileName = "events.txt";
	static Random random =new Random();
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		// Validate the Input Arguments
		validateInputArgs(args);
		String fileName = args[0];
		double epsilon = Double.valueOf( args[1] );
		double delta = Double.valueOf( args[2] );
		double reportTime = Double.valueOf( args[3] );
		
		Stream<String> fileStream = readFile();
		
		List<String> eventList = fileStream.collect(Collectors.toList());
		double timer = 0;
		
		morris( eventList );
		
		closeStream( fileStream );
	}
	
	/**
	 * This method implements the morris algorithm 
	 * @param predictedN
	 * @return the updated predictedN counter
	 */
	public static void morris(List<String> eventList){
		
		double timer = 0;
		int predictedN = 0;
		int predictedCount = 0;
		
		for(int actualN=0; actualN < eventList.size(); actualN ++){
			// System.out.println("######################## Counter == "+actualN);
			timer += Double.valueOf(eventList.get(actualN));
			
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
			
			predictedCount = ((int) Math.pow(2, predictedN)) - 1;
			// Outputing the result at the report time, setting up the timer back to "0" to get the next report time.
			if(timer >= 1.0){
				System.out.println("Output result Actual "+ actualN );
				System.out.println("Output result Morris   = "+ predictedCount );
				System.out.println(" ************************************************** ");
				timer = 0;
			}
		}
		
		// System.out.println("********* " + timer);
		System.out.println("Output result Actual "+ eventList.size() );
		System.out.println("Output result Morris   = "+ predictedCount );
		
	}
	
	/**
	 * This method implements the morris algorithm 
	 * @param predictedN
	 * @return the updated predictedN counter
	 */
	public static void morrisPlus(List<String> eventList, double epsilon, double delta){
		
		double timer = 0;
		int predictedN = 0;
		int predictedCount = 0;
		
		// This is "S" independent copy for Morris+	:- S > (1 / 2^(e^2)*delta) = O(1/delta)
		int noOfCopy = (int) Math.ceil( 1 / (2 * (Math.pow(epsilon, 2.0) * delta)));
		
		for(int actualN=0; actualN < eventList.size(); actualN ++){
			// System.out.println("######################## Counter == "+actualN);
			timer += Double.valueOf(eventList.get(actualN));
			
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
			
			predictedCount = ((int) Math.pow(2, predictedN)) - 1;
			// Outputing the result at the report time, setting up the timer back to "0" to get the next report time.
			if(timer >= 1.0){
				System.out.println("Output result Actual "+ actualN );
				System.out.println("Output result Morris   = "+ predictedCount );
				System.out.println(" ************************************************** ");
				timer = 0;
			}
		}
		
		// System.out.println("********* " + timer);
		System.out.println("Output result Actual "+ eventList.size() );
		System.out.println("Output result Morris   = "+ predictedCount );
		
	}
	
	public static Stream<String> readFile(){
		
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
