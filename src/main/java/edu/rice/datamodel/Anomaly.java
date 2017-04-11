package edu.rice.datamodel;

public class Anomaly {

	int machineNr;
	int dimension;
	double probability;
	long timestamp;

	public Anomaly(int machineNr, int dimension, double probability, long timestamp) {
		this.machineNr = machineNr;
		this.dimension = dimension;
		this.probability = probability;
		this.timestamp = timestamp;
	}

}