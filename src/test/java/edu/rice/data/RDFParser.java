package edu.rice.data;

import java.io.StringReader;
import java.util.Scanner;

public class RDFParser {

    private static String lineStart = "<http://project-hobbit.eu/resources/debs2017#";
    private static int lineStartSkip = lineStart.length();

    private static final String machine = "<http://www.agtinternational.com/ontologies/I4.0#";
    private static final int machineSkip = machine.length();

    private static final String machine2 = "<http://www.agtinternational.com/ontologies/I4.0#machine> <http://www.agtinternational.com/ontologies/WeidmullerMetadata#Machine_";
    private static final int machineSkip2 = machine2.length();

    private static final String cycle1 = "<http://www.agtinternational.com/ontologies/IoTCore#";
    private static final int cycleSkip1 = cycle1.length();

    private static final String cycle2 = "<http://www.agtinternational.com/ontologies/IoTCore#valueLiteral> \"";
    private static final int cycleSkip2 = cycle2.length();

    private static final String observation1 = "<http://purl.oclc.org/NET/ssnx/ssn#";
    private static final int observationSkip1 = observation1.length();
    private static final int observationSkip2 = observation1.length() + 8;

    private static final String observation2 = "<http://purl.oclc.org/NET/ssnx/ssn#observedProperty> <http://www.agtinternational.com/ontologies/WeidmullerMetadata#_";

    private static final int observationSkip3 = observation2.length();
    private static final int observationSkip4 = observation1.length() + 11;

    private static final String observation3 = "<http://purl.oclc.org/NET/ssnx/ssn#observationResult> <http://project-hobbit.eu/resources/debs2017#Output_";
    private static final int observationSkip5 = observation3.length();

    private static final String output1 = "<http://purl.oclc.org/NET/ssnx/ssn#";
    private static final int outputSkip1 = output1.length();

    private static final String output2 = "<http://purl.oclc.org/NET/ssnx/ssn#hasValue> <http://project-hobbit.eu/resources/debs2017#Value_";
    private static int outputSkip2 = output2.length();

    private static final String value1 = "<http://www.agtinternational.com/ontologies/IoTCore#";
    private static final int valueSkip1 = value1.length();

    private static final String value2 = "<http://www.agtinternational.com/ontologies/IoTCore#valueLiteral> \"";
    private static final int valueSkip2 = value2.length();

    private static final char[] Observation = { 'O', 'b', 's', 'e', 'r', 'v', 'a', 't', 'i', 'o', 'n', '\0' };
    private static final char[] ObservationGroup = { 'O', 'b', 's', 'e', 'r', 'v', 'a', 't', 'i', 'o', 'n', 'G', 'r', 'o', 'u', 'p', '\0' };
    private static final char[] Timestamp = { 'T', 'i', 'm', 'e', 's', 't', 'a', 'm', 'p', '\0' };
    private static final char[] Value = { 'V', 'a', 'l', 'u', 'e', '\0' };

    static int machineIndex=0;
    static int dimension=0;
    static double value = 0;
    static int observationNumber=0;
    static int outIndex;

    public static void processData(byte[] bytes) {
        StringReader targetReader = new StringReader(new String(bytes));
        Scanner s = new Scanner(targetReader);

        while (s.hasNextLine()) {
            parse(s.nextLine());
        }
    }

    private static int findCharacter(char[] chars, char c, int start) {

        while (chars[start] != c) {
            start++;
        }

        return start;
    }

    private static void parse(String line) {

        // here we check the
        int i = lineStartSkip;

        char[] chars = line.toCharArray();

        boolean isObservation = true;
        boolean isObservationGroup = true;
        boolean isValue = true;
        boolean isTimestamp = true;

        int j = 0;

        // figure out what we are dealing with....
        while (chars[i] != '_') {

            if (isObservationGroup) {
                isObservationGroup = ObservationGroup[j] == chars[i];
            }

            if (isObservation) {
                isObservation = Observation[j] == chars[i];
            }

            if (isValue) {
                isValue = Value[j] == chars[i];
            }

            if (isTimestamp) {
                isTimestamp = Timestamp[j] == chars[i];
            }

            // we don't care about the others.
            if (!isObservation && !isValue && !isObservationGroup && !isTimestamp) {
                return;
            }

            j++;
            i++;
        }

        // skip the '_'
        i++;

        // find the character >
        j = findCharacter(chars, '>', i);

        // extract the index of the thing...
        observationNumber = NumberParser.getIntegerUnsafe(new String(chars, i, j - i));

        // skip "> "
        i = j + 2;

        // we ware dealing wit an observation
        if (isObservation) {
            // here we only care about the dimension...
            if (chars[i + observationSkip1] == 'o' && chars[i + observationSkip2] == 'P') {
                i = findCharacter(chars, '_', i + observationSkip3) + 1;
                j = findCharacter(chars, '>', i);

                dimension = NumberParser.getIntegerUnsafe(new String(chars, i, j - i));
                System.out.println("Observation_" + observationNumber + ", dimension : " + dimension);
                return;
            }
        }

        // if we are dealing with an observation group
        if (isObservationGroup) {
            // in observation we only are interested in the machine...
            if (chars[i + machineSkip] != 'm') {
                return;
            }

            j = findCharacter(chars, '>', i + machineSkip2);
            i += machineSkip2;

            machineIndex = NumberParser.getIntegerUnsafe(new String(chars, i, j - i));
        }

        //if(isTimestamp) {

        //}

        if (isValue) {
            if (chars[i + valueSkip1] != 'v') {
                return;
            }

            j = findCharacter(chars, '"', i + valueSkip2);
            i += valueSkip2;

            String myValue = new String(chars, i, j - i);


            if (myValue.charAt(1) != 'A') {

                value = NumberParser.getDouble(myValue);

                System.out.println(machineIndex + "," + dimension + "," + value);
            }
        }
    }


}
