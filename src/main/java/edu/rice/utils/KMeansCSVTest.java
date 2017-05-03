//package edu.rice.utils;
//
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.util.Scanner;
//
//import edu.rice.kmeans.CircularQueue;
//import edu.rice.kmeans.KMeans;
//import edu.rice.metadata.MetadataManager;
//import edu.rice.output.OutputGenerator;
//
//public class KMeansCSVTest {
//
//	// Main function to test the KMeans here:
//	public static void main(String[] args) {
//
//		MetadataManager.getInstance().readMetaData("./molding_machine_5000dp.metadata.data");
//
//		// Parameters provided by DEBS:
//		int numPoints = 10; // windowSize
//
//		// Parameters for anomaly detection:
//		double _thresholdProbability = 0.005; // DEBS: "Td"
//
//		// Results:
//		boolean hasAnomalies;
//		double finalThreshold;
//
//		// Create once:
//		KMeans kmeans = new KMeans();
//
//		try {
//			Scanner scan = new Scanner(new File("./DEBS2017_Windows.csv"));
//
//			while (scan.hasNextLine()) {
//
//				String line = scan.nextLine();
//				String[] tokens = line.split(",");
//
//				int timestamp = Integer.parseInt(tokens[0]);
//
//				int machineNr = Integer.parseInt(tokens[1]);
//				int dimensionNr = Integer.parseInt(tokens[2]);
//				int noOfClusters = Integer.parseInt(tokens[3]);
//
//				// if this is not a statefull dimension then return
//				if (MetadataManager.getInstance().getClusterNr(machineNr, dimensionNr) == 0)
//					return;
//
//				// List of points:
//				CircularQueue dataWindow = new CircularQueue(numPoints);
//				for (int i = 4; i < tokens.length; i++) {
//					// Add points:
//					dataWindow.insert(Double.parseDouble(tokens[i]));
//				}
//
//				hasAnomalies = kmeans.performAllCalculation(noOfClusters, dataWindow, _thresholdProbability);
//				if (hasAnomalies) {
//					finalThreshold = kmeans.getThreshold();
//					OutputGenerator.outputAnomaly(machineNr, dimensionNr, finalThreshold, timestamp);
//
//				}
//
//			}
//		} catch (FileNotFoundException fnfe) {
//			System.out.println(fnfe);
//		}
//	}
//
//}
