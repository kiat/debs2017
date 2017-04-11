package edu.rice.kmeans;


public class Point {

	// Point value:
	private double x = 0;
	
	// Associated cluster number:
	private int clusterNumber = -1;

	// Constructor:
	public Point(double x) {
		this.setX(x);
	}

	
	/*
	 * Setters and Getters:
	 */
	public void setX(double x) {
		this.x = x;
	}

	public double getX() {
		return this.x;
	}

	public void setCluster(int n) {
		this.clusterNumber = n;
	}

	public int getCluster() {
		return this.clusterNumber;
	}

	
	/**
	 * Calculates the distance between two points.
	 * Just the absolute difference.
	 */
	protected static double distance(Point p, Point centroid) {
		return Math.abs(centroid.getX() - p.getX());
	}


	/**
	 * Auxiliary function:
	 * Converts point to String.
	 */
	public String toString() {
		return "(" + String.format("%.2f", x) + ", " + clusterNumber  + ")";
	}
}