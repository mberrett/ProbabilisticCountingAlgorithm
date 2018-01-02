package com.fordham.cisc.project;

/**********************************************************************
* CISC-5835 				   : Big Data Algorithm
* Final Programming Assignment : Approximate counting using Morris++ Algorithm
* Project Team Size 		   : 4 Person
* Project Team (4) 			   : Vaibhav Dixit <vdixit@fordham.edu>, Matias Berretta Magarinos <mberrettamagarinos@fordham.edu>
* 				                 Rohini Mandge <rmandge@fordham.edu>, Kwami Nyaku <kwami.nyaku@gmail.com>
* Submission Date 			   : 12 December 2017
*
* This program generates the random events and writes into a file (events.txt).
* time.
*
* This program takes the following input argument 
* - Number of events to be generated    - Integer
* - Granularity                         - Normal distribution of the generated events. ( Floating point number between 0(excluding) and 1(excluding) ) 
* 
* Details about assignment is at http://www.dsm.fordham.edu/~agw/big-data-alg/hw/project-description.pdf
* 
* Author: Vaibhav Dixit <vdixit@fordham.edu>
* 
* Date: 12 December 2017
*
**********************************************************************/

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author vdixit
 *
 */
public class EventGenerator {

	/**
	 * @param args
	 */
	
	// Logger instance to log the error message. 
	static Logger LOGGER = Logger.getLogger("GenEvents");
	// Name of the event file to ge generated.
	static final String fileName = "events.txt";
	
	/**
	 * This is the main method of the program. 
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		//System.out.println("Hello Java");
		long startTime = System.currentTimeMillis();
		
		if(!(args.length ==2) ){
			errorMessage("Please pass the required argument to the program - (NumberOfEvents , Granularity) ", true, 0);
		}
		
		int noOfEvents = 0;
		try {
			noOfEvents = Integer.valueOf(args[0]);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			errorMessage("Number of events has to be integer number", true, null);
			// e.printStackTrace();
			// Program terminating message
			errorMessage(null, true, 0);
		}
		
		double granularity = 0;
		try {
			granularity = Double.valueOf(args[1]);
			if(0 >= granularity || granularity >= 1.0){
				errorMessage("Granularity has to be a floating point number and between 0 and 1", true, 0);
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			errorMessage("Granularity has to be a floating point number and less then 1", true, null);
			// e.printStackTrace();
			// Program terminating message
			errorMessage("", true, 0);
		}
		
		//System.out.println("noOfEvents == " + noOfEvents);
		//System.out.println("granularity == " + granularity);
		
		List<Double> eventList = GanerateRandomEvents(noOfEvents, granularity);
		
		// Write event list to a file
		WriteToFile(eventList);
		
		long endTime = System.currentTimeMillis();
		
		System.out.println(" Elapse Time (In seconds) :- " + ((endTime - startTime)/1000.0));
		
	}
	
	/**
	 * This method generates the random event 
	 * 
	 * @param noOfEvents - Number of events to be generated.
	 * @param granularity - Normal distribution range of generated events.
	 * @return List of generated Events
	 */
	public static List<Double> GanerateRandomEvents(int noOfEvents, double granularity){
		
		System.out.println(" ********* Generating Random Events and saving in events file ********* ");
		List<Double> eventList = new ArrayList<Double>();
		
		for(int counter=0; counter < noOfEvents; counter++){
			// System.out.println(randonGenrator.nextDouble());
			double tmp = ThreadLocalRandom.current().nextDouble(0, granularity);
			//System.out.println(tmp);
			eventList.add(tmp);
		}
		
		return eventList;
	}
	
	/**
	 * Utility method to print the console message with appropriate log level. Also, terminate the program in case of a severe error.
	 * 
	 * @param msg - String message to print on console
	 * @param isError - Log Level
	 * @param exitCode - System exit code in case of termination
	 */
	public static void errorMessage(String msg, boolean isError, Integer exitCode){
		
		if (msg != null && !(msg.equals(""))) {
			if (isError)
				LOGGER.log(Level.SEVERE, msg);
			else
				LOGGER.log(Level.WARNING, msg);
		}
		if(exitCode != null){
			LOGGER.log(Level.SEVERE, "******* Terminating program ******* ");
			System.exit(0);
		}
	}
	
	/**
	 * This method is to write the random generated events in the event file.
	 * 
	 * @param eventList
	 */
	public static void WriteToFile(List<Double> eventList){
		
		FileWriter fw = null;
		BufferedWriter bw = null;
		PrintWriter out = null;
		try {
			fw = new FileWriter(fileName);
			bw = new BufferedWriter(fw);
			out = new PrintWriter(bw);
			for(double event : eventList){
				out.println( event );
			}
			
			out.close();
		} catch (IOException e) {
			// exception handling left as an exercise for the reader
		} finally {

			try {
				if (out != null)
					out.close();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			try {
				if (bw != null)
					bw.close();
			} catch (IOException e) {
				// exception handling left as an exercise for the reader
			}
			try {
				if (fw != null)
					fw.close();
			} catch (IOException e) {
				// exception handling left as an exercise for the reader
			}
		}
	}

}
