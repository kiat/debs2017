//package edu.rice.parser;
//
//import edu.rice.datamodel.ParsedData;
//import edu.rice.system.Controller;
//
//import java.nio.ByteBuffer;
//import java.nio.CharBuffer;
//import java.nio.charset.Charset;
//import java.text.ParseException;
//import java.util.ArrayList;
//
//public class RDFParserToSensordata {
//
//	private static String lineStart = "<http://project-hobbit.eu/resources/debs2017#";
//	private static int lineStartSkip = lineStart.length();
//
//	private static final String machine = "<http://www.agtinternational.com/ontologies/I4.0#";
//	private static final int machineSkip = machine.length();
//
//	private static final String machine2 = "<http://www.agtinternational.com/ontologies/I4.0#machine> <http://www.agtinternational.com/ontologies/WeidmullerMetadata#Machine_";
//	private static final int machineSkip2 = machine2.length();
//
//	private static final String observation1 = "<http://purl.oclc.org/NET/ssnx/ssn#";
//	private static final int observationSkip1 = observation1.length();
//	private static final int observationSkip2 = observation1.length() + 8;
//
//	private static final String observation2 = "<http://purl.oclc.org/NET/ssnx/ssn#observedProperty> <http://www.agtinternational.com/ontologies/WeidmullerMetadata#_";
//	private static final int observationSkip3 = observation2.length();
//
//	private static final String value1 = "<http://www.agtinternational.com/ontologies/IoTCore#";
//	private static final int valueSkip1 = value1.length();
//
//	private static final String value2 = "<http://www.agtinternational.com/ontologies/IoTCore#valueLiteral> \"";
//	private static final int valueSkip2 = value2.length();
//
//	private static final char[] Observation = { 'O', 'b', 's', 'e', 'r', 'v', 'a', 't', 'i', 'o', 'n', '\0' };
//	private static final char[] ObservationGroup = { 'O', 'b', 's', 'e', 'r', 'v', 'a', 't', 'i', 'o', 'n', 'G', 'r', 'o', 'u', 'p', '\0' };
//	private static final char[] Timestamp = { 'T', 'i', 'm', 'e', 's', 't', 'a', 'm', 'p', '\0' };
//	private static final char[] Value = { 'V', 'a', 'l', 'u', 'e', '\0' };
//
//	public RDFParserToSensordata(long messageCount) {
//		this.messageCount = messageCount;
//	}
//
//	long messageCount=0;
//	int machineIndex = 0;
//	int dimension = 0;
//	double value = 0;
//	int timestampIndex;
//
//	public ArrayList<ParsedData> results=new ArrayList<ParsedData>(500);
//	
//	
//	public ArrayList<ParsedData> processData(byte[] bytes) throws ParseException {
//
//		ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
//		Charset utf8 = Charset.forName("UTF-8");
//		CharBuffer charBuffer = utf8.decode(byteBuffer);
//
//		while (charBuffer.hasRemaining()) {
//			int i = parse(charBuffer);
//			i = findCharacter(charBuffer, '\n', i);
//			charBuffer = charBuffer.subSequence(i + 1, charBuffer.length());
//		}
//		
//		
//		return this.results;
//		
//	}
//
//	private int findCharacter(CharBuffer chars, char c, int start) {
//
//		while (chars.charAt(start) != c) {
//			start++;
//		}
//
//		return start;
//	}
//
//	private int parse(CharBuffer line) throws ParseException {
//
//		// here we check the
//		int i = lineStartSkip;
//
//		boolean isObservation = true;
//		boolean isObservationGroup = true;
//		boolean isValue = true;
//		boolean isTimestamp = true;
//
//		int j = 0;
//
//		// figure out what we are dealing with....
//		while (line.charAt(i) != '_') {
//
//			if (isObservationGroup) {
//				isObservationGroup = ObservationGroup[j] == line.charAt(i);
//			}
//
//			if (isObservation) {
//				isObservation = Observation[j] == line.charAt(i);
//			}
//
//			if (isValue) {
//				isValue = Value[j] == line.charAt(i);
//			}
//
//			if (isTimestamp) {
//				isTimestamp = Timestamp[j] == line.charAt(i);
//			}
//
//			// we don't care about the others.
//			if (!isObservation && !isValue && !isObservationGroup && !isTimestamp) {
//				return i;
//			}
//
//			j++;
//			i++;
//		}
//
//		// skip the '_'
//		i++;
//		j = findCharacter(line, '>', i);
//
//		if (isTimestamp) {
//			timestampIndex = NumberParser.getIntegerUnsafe(line.subSequence(i, j));
//		}
//
//		// skip "> "
//		i = j + 2;
//
//		// we ware dealing wit an observation
//		if (isObservation) {
//			// here we only care about the dimension...
//			if (line.charAt(i + observationSkip1) == 'o' && line.charAt(i + observationSkip2) == 'P') {
//				i = findCharacter(line, '_', i + observationSkip3) + 1;
//				j = findCharacter(line, '>', i);
//
//				dimension = NumberParser.getIntegerUnsafe(line.subSequence(i, j));
//			}
//		}
//
//		// if we are dealing with an observation group
//		else if (isObservationGroup) {
//			// in observation we only are interested in the machine...
//			if (line.charAt(i + machineSkip) != 'm') {
//				return i + machineSkip;
//			}
//
//			j = findCharacter(line, '>', i + machineSkip2);
//			i += machineSkip2;
//
//			machineIndex = NumberParser.getIntegerUnsafe(line.subSequence(i, j));
//		}
//
//		// it's a value
//		else if (isValue) {
//			if (line.charAt(i + valueSkip1) != 'v') {
//				return i + valueSkip1;
//			}
//
//			j = findCharacter(line, '"', i + valueSkip2);
//			i += valueSkip2;
//
//			String myValue = line.subSequence(i, j).toString();
//
//			if (myValue.charAt(1) != 'A') {
//				// value = NumberParser.getDouble(myValue);
//				value = Double.parseDouble(myValue);
//				results.add(new ParsedData(this.messageCount, machineIndex, dimension, timestampIndex, value)); 
//
//				// Controller.getInstance().pushData(machineIndex, dimension, timestampIndex, value);
//				// System.out.println(machineIndex+ "," + dimension + "," + timestampIndex+","+ value);
//			}
//		}
//
//		return i;
//	}
//
//}
