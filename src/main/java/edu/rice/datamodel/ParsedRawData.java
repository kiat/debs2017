package edu.rice.datamodel;

public class ParsedRawData {

	private int machineIndex;
	private int dimension;
	private int timestamp;
	private double value;

	public ParsedRawData(int machineIndex, int dimension, int timestamp, double value) {
		super();
		this.machineIndex = machineIndex;
		this.dimension = dimension;
		this.timestamp = timestamp;
		this.value = value;
	}

	public int getMachineIndex() {
		return machineIndex;
	}

	public void setMachineIndex(int machineIndex) {
		this.machineIndex = machineIndex;
	}

	public int getDimension() {
		return dimension;
	}

	public void setDimension(int dimension) {
		this.dimension = dimension;
	}

	public int getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(int timestamp) {
		this.timestamp = timestamp;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

}
