package edu.rice.datamodel;

import java.util.Comparator;

import edu.rice.kmeans.CircularQueue;

public class ParsedData implements Comparator<ParsedData> {

	private long messageCount;
	private int machineNr;
	private int dimensionNr;
	private int timestampIndex;
	private CircularQueue window;
	private double threshold;
	private int noOfClusters;

	public ParsedData(long messageCount, int machineNr, int dimensionNr, int timestampIndex, CircularQueue m_window, int noOfClusters, double threshold) {
		this.messageCount = messageCount;
		this.machineNr = machineNr;
		this.dimensionNr = dimensionNr;
		this.timestampIndex = timestampIndex;
		this.window = m_window;
		this.noOfClusters = noOfClusters;
		this.setThreshold(threshold);
	}

	public long getMessageCount() {
		return messageCount;
	}

	public void setMessageCount(long messageCount) {
		this.messageCount = messageCount;
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

	public int getTimestampIndex() {
		return timestampIndex;
	}

	public void setTimestampIndex(int timestampIndex) {
		this.timestampIndex = timestampIndex;
	}

	public CircularQueue getWindow() {
		return window;
	}

	public void setWindow(CircularQueue window) {
		this.window = window;
	}

	public int getNoOfClusters() {
		return noOfClusters;
	}

	public void setNoOfClusters(int noOfClusters) {
		this.noOfClusters = noOfClusters;
	}

	public double getThreshold() {
		return threshold;
	}

	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}

	@Override
	public int compare(ParsedData o1, ParsedData o2) {
		if (o1.getMessageCount() < o2.getMessageCount())
			return 1;
		else
			return 0;
	}
}