package edu.rice.parser;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.text.ParseException;

import edu.rice.system.Controller;

public class RDFParserFaster {

	// the text to skip from the beginning
	private static String HEADER_SKIP_1_STR = "<http://project-hobbit.eu/resources/debs2017#ObservationGroup_x> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.agtinternational.com/ontologies/I4.0#MoldingMachineObservationGroup> .\n"
			+ "<http://project-hobbit.eu/resources/debs2017#ObservationGroup_0> <http://purl.oclc.org/NET/ssnx/ssn#observationResultTime> <http://project-hobbit.eu/resources/debs2017#Timestamp_x> .";

	// the skip to the machine number
	private static String MACHINE_SKIP_STR = "<http://project-hobbit.eu/resources/debs2017#ObservationGroup_0> <http://www.agtinternational.com/ontologies/I4.0#machine> <http://www.agtinternational.com/ontologies/WeidmullerMetadata#Machine_";

	// skip to the timestamp
	private static String HEADER_SKIP_2_STR = " .\n<http://project-hobbit.eu/resources/debs2017#ObservationGroup_0> <http://www.agtinternational.com/ontologies/I4.0#observedCycle> <http://project-hobbit.eu/resources/debs2017#Cycle_0> .\n<http://project-hobbit.eu/resources/debs2017#Cycle_0> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.agtinternational.com/ontologies/I4.0#Cycle> .\n<http://project-hobbit.eu/resources/debs2017#Cycle_0> <http://www.agtinternational.com/ontologies/IoTCore#valueLiteral> \"13\"^^<http://www.w3.org/2001/XMLSchema#int> .\n<http://project-hobbit.eu/resources/debs2017#Timestamp_0> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.agtinternational.com/ontologies/IoTCore#Timestamp> .";

	// skip to the index of the timestamp
	private static String TIMESTAMP_SKIP_1_STR = "<http://project-hobbit.eu/resources/debs2017#Timestamp_";

	// skip to the timestamp value
	private static String TIMESTAMP_SKIP_2_STR = " <http://www.agtinternational.com/ontologies/IoTCore#valueLiteral> ";

	// one whole data point
	private static String DATA_POINT_CONTENT = "<http://project-hobbit.eu/resources/debs2017#ObservationGroup_X> <http://www.agtinternational.com/ontologies/I4.0#contains> <http://project-hobbit.eu/resources/debs2017#Observation_X> .\n"
			+ "<http://project-hobbit.eu/resources/debs2017#Observation_X> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.agtinternational.com/ontologies/I4.0#MoldingMachineObservation> .\n"
			+ "<http://project-hobbit.eu/resources/debs2017#Observation_X> <http://purl.oclc.org/NET/ssnx/ssn#observationResult> <http://project-hobbit.eu/resources/debs2017#Output_X> .\n"
			+ "<http://project-hobbit.eu/resources/debs2017#Observation_X> <http://purl.oclc.org/NET/ssnx/ssn#observedProperty> <http://www.agtinternational.com/ontologies/WeidmullerMetadata#_X_X> .\n"
			+ "<http://project-hobbit.eu/resources/debs2017#Output_X> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://purl.oclc.org/NET/ssnx/ssn#SensorOutput> .\n"
			+ "<http://project-hobbit.eu/resources/debs2017#Output_X> <http://purl.oclc.org/NET/ssnx/ssn#hasValue> <http://project-hobbit.eu/resources/debs2017#Value_X> .\n"
			+ "<http://project-hobbit.eu/resources/debs2017#Value_X> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.agtinternational.com/ontologies/I4.0#NumberValue> .\n"
			+ "<http://project-hobbit.eu/resources/debs2017#Value_X> <http://www.agtinternational.com/ontologies/IoTCore#valueLiteral> \"\"^^<http://www.w3.org/2001/XMLSchema#double> .";

	// the skip on the dimension
	private static String DIMENSION_SKIP_STR = "<http://project-hobbit.eu/resources/debs2017#ObservationGroup_0> <http://www.agtinternational.com/ontologies/I4.0#contains> <http://project-hobbit.eu/resources/debs2017#Observation_0> .\n"
			+ "<http://project-hobbit.eu/resources/debs2017#Observation_0> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.agtinternational.com/ontologies/I4.0#MoldingMachineObservation> .\n"
			+ "<http://project-hobbit.eu/resources/debs2017#Observation_0> <http://purl.oclc.org/NET/ssnx/ssn#observationResult> <http://project-hobbit.eu/resources/debs2017#Output_0> .\n"
			+ "<http://project-hobbit.eu/resources/debs2017#Observation_0> <http://purl.oclc.org/NET/ssnx/ssn#observedProperty> <http://www.agtinternational.com/ontologies/WeidmullerMetadata#_";

	// the skip on the value
	private static String VALUE_SKIP_STR = "> .\n<http://project-hobbit.eu/resources/debs2017#Output_0> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://purl.oclc.org/NET/ssnx/ssn#SensorOutput> .\n<http://project-hobbit.eu/resources/debs2017#Output_0> <http://purl.oclc.org/NET/ssnx/ssn#hasValue> <http://project-hobbit.eu/resources/debs2017#Value_0> .\n<http://project-hobbit.eu/resources/debs2017#Value_0> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.agtinternational.com/ontologies/I4.0#NumberValue> .\n<http://project-hobbit.eu/resources/debs2017#Value_0> <http://www.agtinternational.com/ontologies/IoTCore#valueLiteral> ";

	private static String DATA_POINT_SKIP_STR = "\"^^<http://www.w3.org/2001/XMLSchema#double> .";

	// header skips
	private static int HEADER_SKIP_1 = HEADER_SKIP_1_STR.length();
	private static int MACHINE_SKIP = MACHINE_SKIP_STR.length();
	private static int HEADER_SKIP_2 = HEADER_SKIP_2_STR.length();
	private static int TIMESTAMP_SKIP_1 = TIMESTAMP_SKIP_1_STR.length();
	private static int TIMESTAMP_SKIP_2 = TIMESTAMP_SKIP_2_STR.length();

	// data point skips
	private static int DIMENSION_SKIP = DIMENSION_SKIP_STR.length();
	private static int VALUE_SKIP = VALUE_SKIP_STR.length();
	private static int DATA_POINT_SKIP = DATA_POINT_SKIP_STR.length();

	static int machineIndex = 0;
	static int dimension = 0;
	static double value = 0;
	static int timestampIndex;
	static String timestampValue;

	public static void processData(byte[] bytes) throws ParseException {

		ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
		Charset utf8 = Charset.forName("UTF-8");
		CharBuffer charBuffer = utf8.decode(byteBuffer);

		int i = parseHeader(charBuffer);

		// parse the data points
		while (charBuffer.length() - i >= 100) {
			i = parse_data_point(charBuffer, i);
		}

	}

	private static int findCharacter(CharBuffer chars, char c, int start) {

		while (chars.charAt(start) != c) {
			start++;
		}

		return start;
	}

	public static int fast_atoi(CharBuffer data, int start, int end) {

		int tmp = 0;

		char[] s = new char[end - start];

		int j = 0;

		for (int i = start; i < end; i++) {
			s[j] = data.get(i);
			j++;
		}

		tmp = Integer.parseInt(new String(s));

		return tmp;

	}

	private static int parseHeader(CharBuffer data) {
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

		// grab the machine index
		machineIndex = fast_atoi(data, i, j);

		// this is to skip to the new line
		i = j + HEADER_SKIP_2;

		// found the beginning of the timestamp line
		i = findCharacter(data, '\n', i);

		// skip to the timestamp index
		i += TIMESTAMP_SKIP_1;

		// skip to the fist character..
		i++;

		// grab the index of the timestamp
		j = findCharacter(data, '>', i);

		// grab the timestamp index
		timestampIndex = fast_atoi(data, i, j);

		// skip to the date value
		i = j + TIMESTAMP_SKIP_2;

		// skip the the first character of the day
		i++;
		i++;

		char[] timestamValueChar = new char[25];

		int y = 0;
		for (int x = i; x < i + 25; x++) {
			timestamValueChar[y] = data.get(x);
			y++;
		}

		timestampValue = new String(timestamValueChar);

		// correct the timestamp index
		timestampIndex = checkIt(timestampIndex, timestampValue);

		// Push it
		Controller.getInstance().pushData(machineIndex, dimension, checkIt(timestampIndex, timestampValue), value);

		i = i + 25;

		i = i + DATA_POINT_SKIP_STR.length();

		return i;
	}

	static int parse_data_point(CharBuffer data, int i) {

		int j;

		// skip to the approximate position of machine dimension value
		i += DIMENSION_SKIP;

		// skip to the real position
		i = findCharacter(data, '_', i);

		// skip the '_'
		i++;

		// skip the machine
		i = findCharacter(data, '_', i) + 1;

		// figure out where the '>'
		j = findCharacter(data, '>', i);

		// grab the dimension
		dimension = fast_atoi(data, i, j);

		// skip to the approximate position of the value
		i = j + VALUE_SKIP;

		// skip to the real position of the value
		i = findCharacter(data, '"', i);

		// skip the '"'
		i++;

		// find the end
		j = findCharacter(data, '"', i);

		char[] valueChar = new char[j - i];

		int y = 0;
		for (int x = i; x < j; x++) {
			valueChar[y] = data.get(x);
			y++;
		}

		try {
			value = Double.parseDouble(new String(valueChar));
		} catch (NumberFormatException e) {

		}

		i = j + DATA_POINT_SKIP;

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
