package edu.rice.kmeans;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;

import edu.rice.output.OutputGenerator;



public class KMeans {
	
	static ArrayList<Double> window; 
	
	int machineNr;
	int dimensionNr;
	
	
	
	// Only the fist time we call this method. 
	public void newSingleValue(ArrayList<Double> window){
		this.window=window;
		//TODO 
		// run the anomaly detector ... 
		
	}
	
	
	
	
	// each time we get a new value we call this method 
	// this method should rerun the program and report the anomalies. 
	public void newSingleValue(double value){
		
		window.remove(0);
		window.add(value);
		
		
		// rerun the anomaly detector ...
		
		
		// if any Anomalies 
		
		System.out.println(OutputGenerator.getInstance().outputAnomaly(59, 31, 0.004115226337448559, 24)); 

	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	//DELETE THIS:
	private boolean printDebugInfo = false;	

	//Number of Clusters. This metric should be related to the number of points
    private int NUMCLUSTERS = -1;
    
    //Number of Points:
    private int NUMPOINTS = -1;

	// Maximum iterations for KMeans:
	private int MAXITERATIONS = -1;

	// Terminal condition for KMeans:
	private double CLUSTERINGPRECISION = 0.0;	
	
	// Paramters for Anomaly detection:
	private int SMALLERWINDOW = -1;
	private double THRESHOLD = 0.0;
	
    
	// List of points and clusters:
    private List<Point> points;
    private List<Cluster> clusters;
	
	
	// Results:
	double resultThreshold = -1;

	 
	// Actual Constructor:
	public KMeans(int _numClusters, int _maxIterations, double _clusteringPrecision, ArrayList<Double> _points, int _smallerWindowSize, double _thresholdProbability) {
	
		//Initialize the data structures: 
		points = new ArrayList<Point> ();
		clusters = new ArrayList<Cluster> ();				
		
		//Maximum iterations for "KMeans":
		this.MAXITERATIONS = _maxIterations;
		
		// Set clustering precision:
		this.CLUSTERINGPRECISION = _clusteringPrecision;
		
		//Number of clusters:
		this.NUMCLUSTERS = _numClusters;
		
		//Points:
		this.points = new ArrayList<Point> ();
		this.NUMPOINTS = _points.size();
		
		
		//Parameters for anomaly detection:
		this.SMALLERWINDOW = _smallerWindowSize;
		this.THRESHOLD = _thresholdProbability;
		
		
		// The first "NUMCLUSTERS" unique points are the cluster centers:
		HashSet <Double> uniquePoints = new HashSet <Double>();
		int countUnique = 0;
		
		for (int i = 0; i < _points.size(); i++) {			
			if (countUnique < NUMCLUSTERS) {
				if(!uniquePoints.contains(_points.get(i))) {
					uniquePoints.add(_points.get(i));			
					
					//Create clusters here:
					Cluster cluster = new Cluster(countUnique);  // Cluster ID 
					Point centroid = new Point(_points.get(i));
					cluster.setCentroid(centroid);
					clusters.add(cluster);
					
					//Increment unique points count:
					countUnique++;
					
				}				
			}
			
			points.add(new Point(_points.get(i)));
		}
		
		
		// If a given window has less than K distinct values than the number of clusters to be computed 
		// must be equal to the number of distinct values in the window.
		if (countUnique < NUMCLUSTERS) {
			NUMCLUSTERS = countUnique;
		}
	
		
		// Perform the calculation:
		this.performClustering();
		
	}
	
	
	
/*
double probabilityThreshold = 0.005
int windowSize = 10;
int transitionsAmount = 5;
int maxClusteringIterations = 50;
double clusteringPrecision = 0.00001;	
*/	
	
	
	//Test the KMeans here:	
	public static void main(String[] args) {
		
		//Parameters provided by DEBS:		
		int numPoints = 10;      // windowSize
		int numClusters = 10;		//NOTE: Change this appropriately
		int maxIterations = 50;		// Maximum iterations for "KMeans"
		double _clusteringPrecision = 0.00001; 
		
		
		//Parameters for anomaly detection:
		int _smallerWindowSize = 5; 		//DEBS: "N"
		double _thresholdProbability = 0.005; //DEBS: "Td"
		

		//List of points:
		ArrayList<Double> _points = new ArrayList<Double> ();
		
		
		
		// Results:
		boolean hasAnomalies;
		double finalThreshold;	
			
		
		//DELETE THIS: My timestamp
		int myTimeStamp = 0;
		
		try {
			Scanner scan = new Scanner(new File("./src/main/resources/106.csv"));
			while(scan.hasNextLine()){
				String line = scan.nextLine();
				String[] tokens = line.split(";");

				
				// Add points:
				_points.add(Double.parseDouble(tokens[tokens.length-1]));	

				// When window size matches, run algorithm:
				if (_points.size() == numPoints) {		
					
				
					// This is how "KMeans" should be run for each window:
					KMeans kmeans = new KMeans(numClusters, maxIterations, _clusteringPrecision, _points, _smallerWindowSize, _thresholdProbability);
					
					hasAnomalies = kmeans.performAnomalyDetection();
					
					
					
					if (hasAnomalies) {
						finalThreshold = kmeans.getThreshold();
						
						System.out.println("TimeStamp: " + (myTimeStamp + numPoints - _smallerWindowSize - 1));
						System.out.println("Threshold: " + finalThreshold);
					}
				
				
					
					_points.remove(0);	// Slide window 1 place.
					myTimeStamp = myTimeStamp + 1;
					
				}
				
			}	

			
		}
		catch (FileNotFoundException fnfe) {
			System.out.println(fnfe);
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
	public boolean performAnomalyDetection() {	
		if (printDebugInfo) {
			System.out.println("Inside Anomaly Detection!");
		} 
	
		// Clear previous results:
		resultThreshold = -1;
	
		// Data structures:
		int rowSum[] = new int[NUMCLUSTERS];
		int count[][] = new int[NUMCLUSTERS][NUMCLUSTERS];
		double transition[][] = new double[NUMCLUSTERS][NUMCLUSTERS];			
		
		// Count pairwise occurrences:
		int firstCluster, secondCluster;
		for (int i = 0; i < points.size() - 1; i++) {
			firstCluster = points.get(i).getCluster();
			secondCluster = points.get(i+1).getCluster();
			
			count[firstCluster][secondCluster] += 1;
			rowSum[firstCluster] += 1;
		}
		
		
		// Create transition matrix:
		for (int i = 0; i < NUMCLUSTERS; i++) {
			for (int j = 0; j < NUMCLUSTERS; j++) {
				if (count[i][j] > 0) {
					transition[i][j] = ((double) count[i][j]) / rowSum[i];
				}
			}
		}
		
		// Additional parameters:
		double curThreshold = 1.0;
		double storeTransition[] = new double[points.size()-1];


		// New anomaly detection code: 2.1
		curThreshold = 1.0;
		for (int i = (NUMPOINTS - SMALLERWINDOW - 1) ; i < NUMPOINTS - 1; i++) {
			firstCluster = points.get(i).getCluster();
			secondCluster = points.get(i+1).getCluster();
			
			curThreshold *= transition[firstCluster][secondCluster];
		}

		if (curThreshold < THRESHOLD) {
			//System.out.println("New logic anomaly detected: " + points.get((NUMPOINTS - SMALLERWINDOW - 1)).getX() + ", " + curThreshold );
			resultThreshold = curThreshold;
			return true;	// Anomaly detected
		}
			
		return false;	// Anomaly not found.
	}
	
    
	// The process to calculate the K Means, with iterating method.
    public void performClustering() {
    	
        boolean finish = false;
        int iteration = 0;
        
		
        // Add in new data, one at a time, recalculating centroids with each new one. 
        while(!finish) {
    
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
        	
        	for(int i = 0; i < lastCentroids.size(); i++) {
        		distance += Point.distance(lastCentroids.get(i), currentCentroids.get(i));
        	}
        		
		
			// Take into account clustering precision:
        	if(distance < CLUSTERINGPRECISION) {
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
    	for(Cluster cluster : clusters) {
    		cluster.clear();
    	}
    }
    
    
    private List<Point> getCentroids() {
    	
    	List<Point> centroids = new ArrayList<Point>(NUMCLUSTERS);
    	
    	for(Cluster cluster : clusters) {
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
		
		
		//New Logic:
		double clusterCentroid = 0.0;    
		
		// Check each point against each cluster:
        for(Point point : points) {
        	min = max;
            for(int i = 0; i < NUMCLUSTERS; i++) {
            	Cluster c = clusters.get(i);
                distance = Point.distance(point, c.getCentroid());
                if(distance < min){
                    min = distance;
                    cluster = i;
					clusterCentroid = c.getCentroid().getX();
                }
				
				// New Logic: If a point is equi-distant from two clusters put it in the higher cluster.
				else if (distance == min) {
					if(c.getCentroid().getX() > clusterCentroid) {
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
     *  Update the cluster centroids:
     */
    private void calculateCentroids() {
    	
        for(Cluster cluster : clusters) {
            double sumX = 0;
            List<Point> list = cluster.getPoints();
            int numPoints = list.size();
            
            for(Point point : list) {
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
    
       
}