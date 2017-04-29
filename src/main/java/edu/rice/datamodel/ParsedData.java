package edu.rice.datamodel;

import java.util.Comparator;

public class ParsedData implements Comparator<ParsedData> {

	private long messageCount;
	private int machineNr;
	private int dimensionNr;
	private int timestampIndex;
	private double value;

	public ParsedData(long messageCount, int machineNr, int dimensionNr, int timestampIndex, double value) {
		this.messageCount = messageCount;
		this.machineNr = machineNr;
		this.dimensionNr = dimensionNr;
		this.timestampIndex = timestampIndex;
		this.value = value;
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

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	@Override
	public int compare(ParsedData o1, ParsedData o2) {
		if (o1.getMessageCount() < o2.getMessageCount())
			return 1;
		else
			return 0;
	}
}