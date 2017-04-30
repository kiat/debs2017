package edu.rice.parser;

public class TimestampManager {

	static String tmpTimestampValue = "";
	static int timestampIndextemp = -1;
	static boolean firstTimeCall = true;

	public static synchronized int checkIt(int timestampIndex, String timestampValue) {

		if (firstTimeCall) {
			timestampIndextemp = timestampIndex;
			tmpTimestampValue = timestampValue;
			firstTimeCall = false;
			return timestampIndextemp;
		} else if (!tmpTimestampValue.equals(timestampValue)) {
			tmpTimestampValue = timestampValue;
			timestampIndextemp++;
		}

		return timestampIndextemp;
	}
}