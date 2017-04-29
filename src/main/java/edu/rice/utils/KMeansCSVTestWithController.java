package edu.rice.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import edu.rice.kmeans.CircularQueue;
import edu.rice.kmeans.KMeans;
import edu.rice.metadata.MetadataManager;
import edu.rice.output.OutputGenerator;
import edu.rice.system.Controller;

public class KMeansCSVTestWithController {

	// Main function to test the KMeans here:
	public static void main(String[] args) {

		MetadataManager.getInstance().readMetaData("./molding_machine_5000dp.metadata.data");

		// Parameters provided by DEBS:
		int numPoints = 10; // windowSize

		// Parameters for anomaly detection:
		double _thresholdProbability = 0.005; // DEBS: "Td"

		// Results:
		boolean hasAnomalies;
		double finalThreshold;

		// Create once:
		KMeans kmeans = new KMeans();

		int counter=0;
		
		try {
			Scanner scan = new Scanner(new File("./rawdata.csv"));

			while (scan.hasNextLine()) {

				String line = scan.nextLine();
				String[] tokens = line.split(",");

				
				int machineNr = Integer.parseInt(tokens[0]);
				int dimensionNr = Integer.parseInt(tokens[1]);
				int timestamp = Integer.parseInt(tokens[2]);
				double value=Double.parseDouble(tokens[3]);

				Controller.getInstance().pushData(machineNr, dimensionNr, timestamp, value);
			}
		} catch (FileNotFoundException fnfe) {
			System.out.println(fnfe);
		}
	}

}
