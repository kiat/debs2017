package edu.rice.kmeans;

import java.util.HashSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;







//DELETE THIS:
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;








public class KMeans {

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
	ArrayList<Integer> resultAnomalies = new ArrayList<Integer>();
	ArrayList<Double> resultThreshold = new ArrayList<Double>();

	 
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
		this.SMALLERWINDOW = _smallerWindowSize + 1;
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
	

    	//Print Initial state
		//System.out.println("Printing Initial State: ");
    	//plotClusters();

		
		// Perform the calculation:
		this.performClustering();
		
		
		//Perform anomaly detection;
		//this.performAnomalyDetection();
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
		ArrayList<Integer> finalAnomalies;
		ArrayList<Double> finalThreshold;	
			
		
		
		//DELETE THIS: My timestamp
		int myTimeStamp = 0;
		
		try {
			Scanner scan = new Scanner(new File("31.csv"));
			while(scan.hasNextLine()){
				String line = scan.nextLine();
				String[] tokens = line.split(";");

				
				// Add points:
				_points.add(Double.parseDouble(tokens[tokens.length-1]));	
				System.out.println("MyT: " + myTimeStamp);

				// When window size matches, run algorithm:
				if (_points.size() == numPoints) {		
					
				
					// This is how "KMeans" should be run for each window:
					KMeans kmeans = new KMeans(numClusters, maxIterations, _clusteringPrecision, _points, _smallerWindowSize, _thresholdProbability);
					
					finalAnomalies = kmeans.performAnomalyDetection();
					finalThreshold = kmeans.getThresholds();
					
					//for (int i = 0; i < finalAnomalies.size(); i++) {
						if (finalAnomalies.size() > 0 ) {
							System.out.println("TimeStamp: " + (myTimeStamp + finalAnomalies.get(finalAnomalies.size()-1)));
							System.out.println("Threshold: " + finalThreshold.get(finalAnomalies.size()-1));
						}
						
					//}
					
					
					// Prepare for next run: Delete all the points found as anomalies:
					if (finalAnomalies.size() > 0) {
						
						int startIndex = 1  + finalAnomalies.get(finalAnomalies.size()-1);	// Inclusive
						int endIndex = numPoints;	// Exclusive
						
						if (startIndex == endIndex) {
							_points.clear();
							myTimeStamp = myTimeStamp + numPoints;
						}
						else {
							_points.subList(0, startIndex).clear();
							myTimeStamp = myTimeStamp + startIndex;
						}						
					}
					else {
						_points.remove(0);	// Slide window 1 place.
						myTimeStamp = myTimeStamp + 1;
					}
					
					
					
					
				}
				
			}	

			
		}
		catch (FileNotFoundException fnfe) {
			System.out.println(fnfe);
		}
	
		
		/* 
		
		//My Data:  Create Points:
		for (int i = 0; i < numPoints; i++) {
			_points.add(0.1 * i);
		}	

		// This is how "KMeans" should be run for each window:
    	//KMeans kmeans = new KMeans(numClusters, maxIterations, _clusteringPrecision, _points, _smallerWindowSize, _thresholdProbability);		
		
		*/
		
		
		
		
			

		

		
    }
    
	// Auxiliary function: Prints the clusters:
	private void plotClusters() {
    	for (int i = 0; i < NUMCLUSTERS; i++) {
    		Cluster c = clusters.get(i);
    		c.plotCluster();
    	}
    }
	
	public ArrayList<Double> getThresholds() {
		return resultThreshold;
	}
	
	
	public ArrayList<Integer> performAnomalyDetection() {	
	
		// Clear previous results:
		resultAnomalies.clear();
		resultThreshold.clear();
	
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
		
		// First Window:
		for (int i = 0; i < SMALLERWINDOW - 1; i++) {
			firstCluster = points.get(i).getCluster();
			secondCluster = points.get(i+1).getCluster();
				
			storeTransition[i] = transition[firstCluster][secondCluster];
			curThreshold = curThreshold * storeTransition[i];			
		}
		
		// First window:
		if (curThreshold < THRESHOLD) {
			//System.out.println("Anomaly Index (Type 1) : " + (SMALLERWINDOW - 1));
			//System.out.println(points.get(SMALLERWINDOW - 1).getX() + ", " + curThreshold);
			resultAnomalies.add(0);
			resultThreshold.add(curThreshold);
		}
		
		
		
		// Subsequent Sliding:		
		for (int i = SMALLERWINDOW - 1; i < points.size() - 1; i++) {
			firstCluster = points.get(i).getCluster();
			secondCluster = points.get(i+1).getCluster();
			
			storeTransition[i] = transition[firstCluster][secondCluster];
			curThreshold = (curThreshold * storeTransition[i]) / storeTransition[i - (SMALLERWINDOW - 1)];
			
			
			if (curThreshold < THRESHOLD) {
				//System.out.println("Anomaly Index (Type 2): " + (i+1));
				//System.out.println(points.get(i+1).getX() + ", " + curThreshold);
				resultAnomalies.add(i+1 - (SMALLERWINDOW - 1));
				resultThreshold.add(curThreshold);
			}
				
		}
			
		return resultAnomalies;
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