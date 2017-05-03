package edu.rice.kmeans;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import edu.rice.output.OutputGenerator;
import edu.rice.utils.Constants;

public class KMeansMultiThread  extends Thread {


	private int machineNr; 
	private int dimensionNr;
	private int timestamp; 
	
	//Number of Clusters. This metric should be related to the number of points
    private int noOfClusters = -1;
    
    //Number of Points:
    private int noOfPoints = -1;

	// Paramters for Anomaly detection:
	private double threshold = 0.0;
	
	
	// List of points and clusters:
    private CircularQueue points;
    private List<Cluster> clusters;
    
    private boolean isAnomaly=false; 
	
	
	// Results:
	double resultThreshold = -1;
	
	//Default Constructor:
	public KMeansMultiThread(int machineNr, int dimensionNr, int noOfClusters, CircularQueue dataWindow, double threshold, int timestamp) {

		this.machineNr=machineNr;
		this.dimensionNr=dimensionNr;
		this.noOfClusters = noOfClusters;

		
		this.points = dataWindow;
		this.noOfPoints = dataWindow.size();
		
		this.threshold = threshold;
		this.timestamp=timestamp;


		//Initialize the data structures: 
		// Clusters:
		clusters = new ArrayList<Cluster> (noOfClusters);				
	}

	
	/**
	 * 
	 */
	public void run(){
		if(performAllCalculation()){
			OutputGenerator.outputAnomaly(machineNr, dimensionNr, resultThreshold, timestamp);
		}
	}
	

	// Actual computation:
	public boolean performAllCalculation() {
		
		// The first "NUMCLUSTERS" unique points are the cluster centers:
		HashSet <Double> uniquePoints = new HashSet <Double>();
		int countUnique = 0;
		
		// Current value:
		double curValue;
		for (int i = 0; i < noOfPoints; i++) {			
			if (countUnique < noOfClusters) {
				curValue = points.get(i).getX();
				if(!uniquePoints.contains(curValue)) {
					uniquePoints.add(curValue);			
					
					//Create clusters here:
					Cluster cluster = new Cluster(countUnique);  // Cluster ID 
					Point centroid = new Point(curValue);
					cluster.setCentroid(centroid);
					clusters.add(cluster);
					
					//Increment unique points count:
					countUnique++;	
				}				
			}
		}
			
		// If a given window has less than K distinct values than the number of clusters to be computed 
		// must be equal to the number of distinct values in the window.
		if (countUnique < noOfClusters) {
			noOfClusters = countUnique;
		}
			
		// Perform the clustering:
		this.performClustering();
		

		// Perform anomaly detection:
		return this.performAnomalyDetection();
	}
	
	

	
	// Returns if window has anomaly or not:
	private boolean performAnomalyDetection() {	
	
		// Data structures:
		int rowSum[] = new int[noOfClusters];
		int count[][] = new int[noOfClusters][noOfClusters];
		double transition[][] = new double[noOfClusters][noOfClusters];			
		
		// Count pairwise occurrences:
		int firstCluster, secondCluster;
		for (int i = 0; i < points.size() - 1; i++) {
			firstCluster = points.get(i).getCluster();
			secondCluster = points.get(i+1).getCluster();
			
			count[firstCluster][secondCluster] += 1;
			rowSum[firstCluster] += 1;
		}
		
		
		// Create transition matrix:
		for (int i = 0; i < noOfClusters; i++) {
			for (int j = 0; j < noOfClusters; j++) {
				if (count[i][j] > 0) {
					transition[i][j] = ((double) count[i][j]) / rowSum[i];
				}
			}
		}
		
		// Additional parameters:
		double curThreshold = 1.0;


		// New anomaly detection code: 2.1
		curThreshold = 1.0;
		for (int i = (noOfPoints - Constants.SMALLER_WINDOW - 1) ; i < noOfPoints - 1; i++) {
			firstCluster = points.get(i).getCluster();
			secondCluster = points.get(i+1).getCluster();
			
			curThreshold *= transition[firstCluster][secondCluster];
		}

		
		if (curThreshold < threshold) {
			resultThreshold = curThreshold;
			return true;	// Anomaly detected
		}

		return false;	// Anomaly not found.
	}
	
    
	// The process to calculate the K Means, with iterating method.
    private void performClustering() {
    	
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
        	if(distance < Constants.CLUSTERING_PRECISION) {
        		finish = true;
        	} 				
				
			// DEBS: Do not run more than maximum iterations:
			if (iteration == Constants.MAX_CLUSTERING_ITERATION) {
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
    	List<Point> centroids = new ArrayList<Point>(noOfClusters);
    	
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
        for(int j = 0; j < points.size(); j++) {
			Point point = points.get(j);
        	min = max;
            for(int i = 0; i < noOfClusters; i++) {
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
    
    
	
	public int getMachineNr() {
		return machineNr;
	}


	public void setMachineNr(int machineNr) {
		this.machineNr = machineNr;
	}


	public int getDimensionNr() {
		return dimensionNr;
	}


	public void setDimensionNr(int dimensionNr) {
		this.dimensionNr = dimensionNr;
	}


	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}

	// Get threshold if anomaly detected:
	public double getThreshold() {
		return resultThreshold;
	}


	public boolean isAnomaly() {
		return isAnomaly;
	}


	public void setAnomaly(boolean isAnomaly) {
		this.isAnomaly = isAnomaly;
	}
	
       
}