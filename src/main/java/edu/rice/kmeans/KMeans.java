package edu.rice.kmeans;

//DELETE THIS:
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

import java.util.LinkedList;

import edu.rice.output.OutputGenerator;
import edu.rice.utils.Constants;

public class KMeans extends Thread {

	// Review this logic:
	private int timeStampCounter = 0;

	// DELETE THIS:
	private boolean printDebugInfo = false;

	// Number of Clusters. This metric should be related to the number of points
	private int GLOBALNUMCLUSTER = -1;
	private int NUMCLUSTERS = -1;

	// Number of Points:
	private int NUMPOINTS = -1;

	// Maximum iterations for KMeans:
	private int MAXITERATIONS = Constants.maxClusteringIterations;

	// Terminal condition for KMeans:
	private double CLUSTERINGPRECISION = Constants.clusteringPrecision;

	// Paramters for Anomaly detection:
	private int SMALLERWINDOW = Constants.SMALLERWINDOW;
	private double THRESHOLD = 0.0;

	// List of points and clusters:
	private List<Point> points;
	private List<Cluster> clusters;

	// Results:
	double resultThreshold = -1;

	// Basic Constructor: Just passes the parameters:
	public KMeans(int _numClusters, double _thresholdProbability) {

		// Older Code:
		// Initialize the data structures:
		points = new ArrayList<Point>();
		clusters = new ArrayList<Cluster>();

		// Number of clusters:
		this.GLOBALNUMCLUSTER = _numClusters;
		this.NUMCLUSTERS = _numClusters;

		// Parameters for anomaly detection:
		this.THRESHOLD = _thresholdProbability;

	}

	// The first call provides the initial window:
	public void newSingleValue(ArrayList<Double> _points) {
		// Points:
		this.points = new ArrayList<Point>(Constants.Window_Size);
		this.NUMPOINTS = _points.size();

		this.timeStampCounter = 0;

		// Create "points":
		for (int i = 0; i < _points.size(); i++) {
			points.add(new Point(_points.get(i)));
		}

		this.performAllCalculations();
	}

//	// each time we get a new value we call this method
//	// this method should rerun the program and report the anomalies.
//	public void newSingleValue(double value) {
//
//		points.remove(0);
//		points.add(new Point(value));
//
//		this.timeStampCounter = this.timeStampCounter + 1;
//
//		this.performAllCalculations();
//	}

	public void performAllCalculations() {
		// New Bug:
		NUMCLUSTERS = GLOBALNUMCLUSTER;
		clusters.clear();

		// The first "NUMCLUSTERS" unique points are the cluster centers:
		HashSet<Double> uniquePoints = new HashSet<Double>();
		int countUnique = 0;

		double curValue = 0.0;
		for (int i = 0; i < points.size(); i++) {
			if (countUnique < NUMCLUSTERS) {
				curValue = points.get(i).getX();
				if (!uniquePoints.contains(curValue)) {
					uniquePoints.add(curValue);

					// Create clusters here:
					Cluster cluster = new Cluster(countUnique); // Cluster ID
					Point centroid = new Point(curValue);
					cluster.setCentroid(centroid);
					clusters.add(cluster);

					// Increment unique points count:
					countUnique++;

				}
			}
		}

		// If a given window has less than K distinct values than the number of
		// clusters to be computed
		// must be equal to the number of distinct values in the window.
		if (countUnique < NUMCLUSTERS) {
			NUMCLUSTERS = countUnique;
		}

		if (printDebugInfo) {
			for (int i = 0; i < points.size(); i++) {
				System.out.print(points.get(i).getX() + " ");
			}
			System.out.println("");

			plotClusters();
		}

		// Perform the calculation:
		this.performClustering();

		boolean hasAnomalies = this.performAnomalyDetection(NUMCLUSTERS, SMALLERWINDOW, THRESHOLD);
		if (hasAnomalies) {
			
			 System.out.println(this.getThreshold()+ ", " +(timeStampCounter + NUMPOINTS - SMALLERWINDOW - 1));

			// This is the Anomaly Output
//			System.out.println(OutputGenerator.getInstance().outputAnomaly(machineNumber, dimensionNumber, getThreshold(), (timeStampCounter + NUMPOINTS - SMALLERWINDOW - 1)));

			 
		}
	}


	// Auxiliary function: Prints the clusters:
	private void plotClusters() {
		for (int i = 0; i < NUMCLUSTERS; i++) {
			Cluster c = clusters.get(i);
			c.plotCluster();
		}
	}

	// Get threshold if anomaly detected:
	public double getThreshold() {
		return resultThreshold;
	}

	// Returns if window has anomaly or not:
	public boolean performAnomalyDetection(int numOfClusters, int smallerWindowSize, double threshhold) {

		// Clear previous results:
		resultThreshold = -1;

		// Data structures:
		int rowSum[] = new int[numOfClusters];
		int count[][] = new int[numOfClusters][numOfClusters];
		double transition[][] = new double[numOfClusters][numOfClusters];

		// Count pairwise occurrences:
		int firstCluster, secondCluster;
		for (int i = 0; i < points.size() - 1; i++) {
			firstCluster = points.get(i).getCluster();
			secondCluster = points.get(i + 1).getCluster();

			count[firstCluster][secondCluster] += 1;
			rowSum[firstCluster] += 1;
		}

		// Create transition matrix:
		for (int i = 0; i < numOfClusters; i++) {
			for (int j = 0; j < numOfClusters; j++) {
				if (count[i][j] > 0) {
					transition[i][j] = ((double) count[i][j]) / rowSum[i];
				}
			}
		}

		// Additional parameters:
		double curThreshold = 1.0;

		// New anomaly detection code: 2.1
		curThreshold = 1.0;
		for (int i = (NUMPOINTS - smallerWindowSize - 1); i < NUMPOINTS - 1; i++) {
			firstCluster = points.get(i).getCluster();
			secondCluster = points.get(i + 1).getCluster();

			curThreshold *= transition[firstCluster][secondCluster];
		}


		// here is the anomaly detection 
		if (curThreshold < threshhold) {
			resultThreshold = curThreshold;
			return true; // Anomaly detected
		}

		return false; // Anomaly not found.
	}

	// The process to calculate the K Means, with iterating method.
	public void performClustering() {

		boolean finish = false;
		int iteration = 0;

		// Add in new data, one at a time, recalculating centroids with each new
		// one.
		while (!finish) {

			// Clears cluster:
			clearClusters(); // Only clears points, keeps centroid.

			// Since centroids are still there, retrieve previous centroids:
			List<Point> lastCentroids = getCentroids();

			// Assign points to the closer cluster
			assignCluster();

			// Calculate new centroids.
			calculateCentroids();

			iteration++;

			// Retrieve new centroids:
			List<Point> currentCentroids = getCentroids();

			// Calculates total distance between new and old Centroids
			double distance = 0;

			for (int i = 0; i < lastCentroids.size(); i++) {
				distance += Point.distance(lastCentroids.get(i), currentCentroids.get(i));
			}

			// Take into account clustering precision:
			if (distance < CLUSTERINGPRECISION) {
				finish = true;
			}

			// DEBS: Do not run more than maximum iterations:
			if (iteration == MAXITERATIONS) {
				finish = true;
			}
		}

	}

	// Clears Cluster: Only clears points, keeps centroid.
	private void clearClusters() {
		for (Cluster cluster : clusters) {
			cluster.clear();
		}
	}

	private List<Point> getCentroids() {

		List<Point> centroids = new ArrayList<Point>(NUMCLUSTERS);

		for (Cluster cluster : clusters) {
			Point aux = cluster.getCentroid();
			Point point = new Point(aux.getX());
			centroids.add(point);
		}

		return centroids;
	}

	/**
	 * Assign points to cluster:
	 */
	private void assignCluster() {
		double max = Double.MAX_VALUE;
		double min = max;
		int cluster = 0;
		double distance = 0.0;

		// New Logic:
		double clusterCentroid = 0.0;

		// Check each point against each cluster:
		for (Point point : points) {
			min = max;
			for (int i = 0; i < NUMCLUSTERS; i++) {
				Cluster c = clusters.get(i);
				distance = Point.distance(point, c.getCentroid());
				if (distance < min) {
					min = distance;
					cluster = i;
					clusterCentroid = c.getCentroid().getX();
				}

				// New Logic: If a point is equi-distant from two clusters put
				// it in the higher cluster.
				else if (distance == min) {
					if (c.getCentroid().getX() > clusterCentroid) {
						min = distance;
						cluster = i;
						clusterCentroid = c.getCentroid().getX();
					}
				}
			}
			point.setCluster(cluster);
			clusters.get(cluster).addPoint(point);
		}
	}

	/**
	 * Update the cluster centroids:
	 */
	private void calculateCentroids() {

		for (Cluster cluster : clusters) {
			double sumX = 0;
			List<Point> list = cluster.getPoints();
			int numPoints = list.size();

			for (Point point : list) {
				sumX += point.getX();
			}

			// New Code: Original code had bugs: (ss107)
			Point centroid = cluster.getCentroid();
			if (numPoints > 0) {
				double newX = sumX / numPoints;
				centroid.setX(newX);
			}
		}
	}

	// Test the KMeans here:
	public static void main(String[] args) {

		// Parameters provided by DEBS:
		int windowSize = 10; // windowSize
		int numClusters = 10; // NOTE: Change this appropriately

		// Parameters for anomaly detection:
		double _thresholdProbability = 0.005; // DEBS: "Td"

		// List of points:
		ArrayList<Double> _points = new ArrayList<Double>();

		// This is how "KMeans" should be run for each window:
		KMeans kmeans = new KMeans(numClusters, _thresholdProbability);

		try {
			Scanner scan = new Scanner(new File("./src/main/resources/31.csv"));
			while (scan.hasNextLine()) {
				String line = scan.nextLine();
				String[] tokens = line.split(";");

				// Add points:
				_points.add(Double.parseDouble(tokens[tokens.length - 1]));

				// When window size matches, run algorithm:
				if (_points.size() == windowSize) {
					break;
				}
			}

			// First Call:
			kmeans.newSingleValue(_points);
			
			LinkedList<Double> d_windows = new LinkedList<Double>();
			

			while (scan.hasNextLine()) {
				String line = scan.nextLine();
				String[] tokens = line.split(";");				
				
				if(d_windows.size() == Constants.Window_Size){
					
					ArrayList<Double> convertedWindow=new ArrayList<Double>(d_windows); 
					kmeans.newSingleValue(convertedWindow);
					System.out.println(convertedWindow);
					
					d_windows.remove();

				}else{
					d_windows.add(Double.parseDouble(tokens[tokens.length - 1]));
					
				}
				
				
				// One point at a time:
//				kmeans.newSingleValue(Double.parseDouble(tokens[tokens.length - 1]));
			}

		} catch (FileNotFoundException fnfe) {
			System.out.println(fnfe);
		}
	}

}