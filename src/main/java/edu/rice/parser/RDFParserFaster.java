package edu.rice.parser;

import edu.rice.system.Controller;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.text.ParseException;

public class RDFParserFaster {
	
	// the text to skip from the beginning
	private static String  HEADER_SKIP_1_STR = "<http://project-hobbit.eu/resources/debs2017#ObservationGroup_x> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.agtinternational.com/ontologies/I4.0#MoldingMachineObservationGroup> .\n"
			+ "<http://project-hobbit.eu/resources/debs2017#ObservationGroup_0> <http://purl.oclc.org/NET/ssnx/ssn#observationResultTime> <http://project-hobbit.eu/resources/debs2017#Timestamp_x> ." ;

	// the skip to the machine number
	private static String MACHINE_SKIP_STR  = "<http://project-hobbit.eu/resources/debs2017#ObservationGroup_0> <http://www.agtinternational.com/ontologies/I4.0#machine> <http://www.agtinternational.com/ontologies/WeidmullerMetadata#Machine_";

	// skip to the timestamp
	private static String HEADER_SKIP_2_STR = " .\n<http://project-hobbit.eu/resources/debs2017#ObservationGroup_0> <http://www.agtinternational.com/ontologies/I4.0#observedCycle> <http://project-hobbit.eu/resources/debs2017#Cycle_0> .\n<http://project-hobbit.eu/resources/debs2017#Cycle_0> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.agtinternational.com/ontologies/I4.0#Cycle> .\n<http://project-hobbit.eu/resources/debs2017#Cycle_0> <http://www.agtinternational.com/ontologies/IoTCore#valueLiteral> \"13\"^^<http://www.w3.org/2001/XMLSchema#int> .\n<http://project-hobbit.eu/resources/debs2017#Timestamp_0> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.agtinternational.com/ontologies/IoTCore#Timestamp> .";

	// skip to the index of the timestamp
	private static String TIMESTAMP_SKIP_1_STR = "<http://project-hobbit.eu/resources/debs2017#Timestamp_"; 

	// skip to the timestamp value
	private static String  TIMESTAMP_SKIP_2_STR = " <http://www.agtinternational.com/ontologies/IoTCore#valueLiteral> ";

	// skip to the end
	private static String HEADER_SKIP_3_STR = "\"^^<http://www.w3.org/2001/XMLSchema#dateTime> .\n"; 

	// skip the date values
	private static String DATE_VALUE_SKIP_1_STR = "2017-01-";
	private static String DATE_VALUE_SKIP_2_STR = "T01:";

	// one whole data point
	private static String DATA_POINT_CONTENT = "<http://project-hobbit.eu/resources/debs2017#ObservationGroup_X> <http://www.agtinternational.com/ontologies/I4.0#contains> <http://project-hobbit.eu/resources/debs2017#Observation_X> .\n" +
	"<http://project-hobbit.eu/resources/debs2017#Observation_X> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.agtinternational.com/ontologies/I4.0#MoldingMachineObservation> .\n" + 
	"<http://project-hobbit.eu/resources/debs2017#Observation_X> <http://purl.oclc.org/NET/ssnx/ssn#observationResult> <http://project-hobbit.eu/resources/debs2017#Output_X> .\n" +
	"<http://project-hobbit.eu/resources/debs2017#Observation_X> <http://purl.oclc.org/NET/ssnx/ssn#observedProperty> <http://www.agtinternational.com/ontologies/WeidmullerMetadata#_X_X> .\n" +
	"<http://project-hobbit.eu/resources/debs2017#Output_X> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://purl.oclc.org/NET/ssnx/ssn#SensorOutput> .\n" +
	"<http://project-hobbit.eu/resources/debs2017#Output_X> <http://purl.oclc.org/NET/ssnx/ssn#hasValue> <http://project-hobbit.eu/resources/debs2017#Value_X> .\n" +
	"<http://project-hobbit.eu/resources/debs2017#Value_X> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.agtinternational.com/ontologies/I4.0#NumberValue> .\n" +
	"<http://project-hobbit.eu/resources/debs2017#Value_X> <http://www.agtinternational.com/ontologies/IoTCore#valueLiteral> \"\"^^<http://www.w3.org/2001/XMLSchema#double> .";

	// the skip on the dimension
	private static String DIMENSION_SKIP_STR = "<http://project-hobbit.eu/resources/debs2017#ObservationGroup_0> <http://www.agtinternational.com/ontologies/I4.0#contains> <http://project-hobbit.eu/resources/debs2017#Observation_0> .\n"+
	"<http://project-hobbit.eu/resources/debs2017#Observation_0> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.agtinternational.com/ontologies/I4.0#MoldingMachineObservation> .\n" +
	"<http://project-hobbit.eu/resources/debs2017#Observation_0> <http://purl.oclc.org/NET/ssnx/ssn#observationResult> <http://project-hobbit.eu/resources/debs2017#Output_0> .\n" +
	"<http://project-hobbit.eu/resources/debs2017#Observation_0> <http://purl.oclc.org/NET/ssnx/ssn#observedProperty> <http://www.agtinternational.com/ontologies/WeidmullerMetadata#_"; 

	//the skip on the value
	private static String VALUE_SKIP_STR = "> .\n<http://project-hobbit.eu/resources/debs2017#Output_0> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://purl.oclc.org/NET/ssnx/ssn#SensorOutput> .\n<http://project-hobbit.eu/resources/debs2017#Output_0> <http://purl.oclc.org/NET/ssnx/ssn#hasValue> <http://project-hobbit.eu/resources/debs2017#Value_0> .\n<http://project-hobbit.eu/resources/debs2017#Value_0> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.agtinternational.com/ontologies/I4.0#NumberValue> .\n<http://project-hobbit.eu/resources/debs2017#Value_0> <http://www.agtinternational.com/ontologies/IoTCore#valueLiteral> "; 

	private static String DATA_POINT_SKIP_STR = "\"^^<http://www.w3.org/2001/XMLSchema#double> ."; 

	
	
	// header skips
	private static int HEADER_SKIP_1 = HEADER_SKIP_1_STR.length();
	private static int MACHINE_SKIP = MACHINE_SKIP_STR.length();
	private static int HEADER_SKIP_2 = HEADER_SKIP_2_STR.length();
	private static int TIMESTAMP_SKIP_1 = TIMESTAMP_SKIP_1_STR.length();
	private static int TIMESTAMP_SKIP_2 = TIMESTAMP_SKIP_2_STR.length();
	private static int HEADER_SKIP_3 = HEADER_SKIP_3_STR.length();
	private static int DATE_VALUE_SKIP_1 = DATE_VALUE_SKIP_1_STR.length();
	private static int DATE_VALUE_SKIP_2 = DATE_VALUE_SKIP_2_STR.length();
	
	
    

	private static String lineStart = "<http://project-hobbit.eu/resources/debs2017#";
	private static int lineStartSkip = lineStart.length();

	private static final String machine = "<http://www.agtinternational.com/ontologies/I4.0#";
	private static final int machineSkip = machine.length();

	private static final String machine2 = "<http://www.agtinternational.com/ontologies/I4.0#machine> <http://www.agtinternational.com/ontologies/WeidmullerMetadata#Machine_";
	private static final int machineSkip2 = machine2.length();

	private static final String observation1 = "<http://purl.oclc.org/NET/ssnx/ssn#";
	private static final int observationSkip1 = observation1.length();
	private static final int observationSkip2 = observation1.length() + 8;

	private static final String observation2 = "<http://purl.oclc.org/NET/ssnx/ssn#observedProperty> <http://www.agtinternational.com/ontologies/WeidmullerMetadata#_";
	private static final int observationSkip3 = observation2.length();

	private static final String value1 = "<http://www.agtinternational.com/ontologies/IoTCore#";
	private static final int valueSkip1 = value1.length();

	private static final String value2 = "<http://www.agtinternational.com/ontologies/IoTCore#valueLiteral> \"";
	private static final int valueSkip2 = value2.length();

	private static final String timestamp1 = "<http://www.agtinternational.com/ontologies/IoTCore#";
	private static final int timestampSkip = timestamp1.length();

	private static final String timestamp2 = "<http://www.agtinternational.com/ontologies/IoTCore#valueLiteral> \"";
	private static final int timestampSkip2 = timestamp2.length();

	private static final char[] Observation = { 'O', 'b', 's', 'e', 'r', 'v', 'a', 't', 'i', 'o', 'n', '\0' };
	private static final char[] ObservationGroup = { 'O', 'b', 's', 'e', 'r', 'v', 'a', 't', 'i', 'o', 'n', 'G', 'r', 'o', 'u', 'p', '\0' };
	private static final char[] Timestamp = { 'T', 'i', 'm', 'e', 's', 't', 'a', 'm', 'p', '\0' };
	private static final char[] Value = { 'V', 'a', 'l', 'u', 'e', '\0' };

	static int machineIndex = 0;
	static int dimension = 0;
	static double value = 0;
	static int timestampIndex;
	static String timestampValue;

	public static void processData(byte[] bytes) throws ParseException {

		ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
		Charset utf8 = Charset.forName("UTF-8");
		CharBuffer charBuffer = utf8.decode(byteBuffer);
		
		
		parseHeader(charBuffer);
		
//
//		while (charBuffer.hasRemaining()) {
//			int i = parse(charBuffer);
//			i = findCharacter(charBuffer, '\n', i);
//			charBuffer = charBuffer.subSequence(i + 1, charBuffer.length());
//		}
	}

	private static int findCharacter(CharBuffer chars, char c, int start) {

		while (chars.charAt(start) != c) {
			start++;
		}

		return start;
	}
	
	
	
	private static int parseHeader(CharBuffer data){
		    int i = HEADER_SKIP_1;
		    int j;

		    // found the beginning of the machine
		    i = findCharacter(data, '\n', i);

		    // skip to the potential start of the machine value
		    i += MACHINE_SKIP;

		    // go to the actual beginning of the machine value
		    i = findCharacter(data, '_', i);

		    // go to the first character
		    i++;

		    // grab the machine number
		    j = findCharacter(data, '>', i);

//		    // grab the machine index
//		    machineIndex = fast_atoi(data + i, j - i);
//
//		    // this is to skip to the new line
//		    i = j + HEADER_SKIP_2;
//
//		    // found the beginning of the timestamp line
//		    i = findCharacter(data, '\n', i);
//
//		    // skip to the timestamp index
//		    i += TIMESTAMP_SKIP_1;
//
//		    // skip to the fist character..
//		    i++;
//
//		    // grab the index of the timestamp
//		    j = findCharacter(data, '>', i);
//
//		    // grab the timestamp index
//		    timestampIndex = (size_t) fast_atoi(data + i, j - i);
//
//		    // skip to the date value
//		    i = j + TIMESTAMP_SKIP_2 + DATE_VALUE_SKIP_1;
//
//		    // skip the the first character of the day
//		    i++;

//		    // grab the day
//		    int day = (data[i] - '0') * 10 + (data[i+1] - '0');
//
//		    // skip to the hours
//		    i += 2 + DATE_VALUE_SKIP_2;
//
//		    // grab the hours
//		    int hours = (data[i] - '0') * 10 + (data[i+1] - '0');
//
//		    // skip ':' to to to minutes
//		    i += 3;
//
//		    // grab the minutes
//		    int minutes = (data[i] - '0') * 10 + (data[i+1] - '0');
//
//		    // figure out the hash
//		    int current_hash = (24 * 60) * day + 60 * hours + minutes;
//
//		    // figure out the timestamp.
//		    timestamp_idx = check_timestamp(current_hash, timestamp_idx);
//
//		    // skip the rest of the header
//		    i += HEADER_SKIP_3;
//
//		    // found the beginning of the data
//		    i = find_character(data, '\n', i);

		    return i;
		
	}
	
	
	
	

	private static int parse(CharBuffer line) throws ParseException {

		// here we check the
		int i = lineStartSkip;

		boolean isObservation = true;
		boolean isObservationGroup = true;
		boolean isValue = true;
		boolean isTimestamp = true;

		int j = 0;

		// figure out what we are dealing with....
		while (line.charAt(i) != '_') {

			if (isObservationGroup) {
				isObservationGroup = ObservationGroup[j] == line.charAt(i);
			}

			if (isObservation) {
				isObservation = Observation[j] == line.charAt(i);
			}

			if (isValue) {
				isValue = Value[j] == line.charAt(i);
			}

			if (isTimestamp) {
				isTimestamp = Timestamp[j] == line.charAt(i);
			}

			// we don't care about the others.
			if (!isObservation && !isValue && !isObservationGroup && !isTimestamp) {
				return i;
			}

			j++;
			i++;
		}

		// skip the '_'
		i++;
		j = findCharacter(line, '>', i);

		if (isTimestamp) {
			timestampIndex = NumberParser.getIntegerUnsafe(line.subSequence(i, j));
		}

		// skip "> "
		i = j + 2;

		// we ware dealing wit an observation
		if (isObservation) {
			// here we only care about the dimension...
			if (line.charAt(i + observationSkip1) == 'o' && line.charAt(i + observationSkip2) == 'P') {
				i = findCharacter(line, '_', i + observationSkip3) + 1;
				j = findCharacter(line, '>', i);

				dimension = NumberParser.getIntegerUnsafe(line.subSequence(i, j));
			}
		}

		// if we are dealing with an observation group
		else if (isObservationGroup) {
			// in observation we only are interested in the machine...
			if (line.charAt(i + machineSkip) != 'm') {
				return i + machineSkip;
			}

			j = findCharacter(line, '>', i + machineSkip2);
			i += machineSkip2;

			machineIndex = NumberParser.getIntegerUnsafe(line.subSequence(i, j));
		}

		// if it's a timestamp
		else if (isTimestamp) {
			// we only are interested in the value
			if (line.charAt(i + timestampSkip) != 'v') {
				return i + timestampSkip;
			}

			j = findCharacter(line, '"', i + timestampSkip2);
			i += timestampSkip2;

			timestampValue = line.subSequence(i, j).toString();
		}

		// it's a value
		else if (isValue) {
			if (line.charAt(i + valueSkip1) != 'v') {
				return i + valueSkip1;
			}

			j = findCharacter(line, '"', i + valueSkip2);
			i += valueSkip2;

			String myValue = line.subSequence(i, j).toString();

			if (myValue.charAt(1) != 'A') {
				// value = NumberParser.getDouble(myValue);
				value = Double.parseDouble(myValue);
				
				Controller. getInstance().pushData(machineIndex, dimension, checkIt(timestampIndex, timestampValue), value);
//				System.out.println(machineIndex+ "," + dimension + "," +  checkIt(timestampIndex, timestampValue) + ", ValueIs: " + value); 
				
     			// System.out.println(machineIndex+ "," + dimension + "," +
				// timestampIndex+","+ value);

//				checkIt(timestampIndex, timestampValue);

			}
		}

		return i;
	}

	static String tmpTimestampValue = "";
	static int timestampIndextemp = -1;
	static boolean firstTimeCall = true;

	public static int checkIt(int timestampIndex, String timestampValue) {

		if (firstTimeCall) {
			timestampIndextemp = timestampIndex;
			tmpTimestampValue = timestampValue;
			firstTimeCall = false;
			return timestampIndextemp;
		}

		if (!tmpTimestampValue.equals(timestampValue)) {
			tmpTimestampValue = timestampValue;
			timestampIndextemp++;
		}
		
		return timestampIndextemp;
	}

}
