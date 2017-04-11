package edu.rice.kmeans;

import java.util.ArrayList;
import java.util.List;

public class Cluster {
	
	
	/* A cluster has the following:
		i) list of points,
		ii) centroid and 
		iii) identifier. 
	*/
	
	public List<Point> points;
	public Point centroid;
	public int id;
	
	
	// Default constructor: Creates an empty cluster:
	public Cluster(int id) {
		this.id = id;
		this.points = new ArrayList<Point>();
		this.centroid = null;
	}

	
	public List<Point> getPoints() {
		return points;
	}
	
	public void addPoint(Point point) {
		points.add(point);
	}

	public void setPoints(List<Point> points) {
		this.points = points;
	}

	public Point getCentroid() {
		return centroid;
	}

	public void setCentroid(Point centroid) {
		this.centroid = centroid;
	}

	public int getId() {
		return id;
	}
	
	
	// Clears the list of points:
	public void clear() {
		points.clear();
	}
	
	
	// Auxiliary Function: Helps us print:
	public void plotCluster() {
		
		System.out.println("[Cluster: " + id + "]");
		System.out.println("[Centroid: " + centroid + "]");

		System.out.println("[Points: \n");
		for(Point p : points) {
			System.out.println(p);
		}
		System.out.println("]");
	}

}