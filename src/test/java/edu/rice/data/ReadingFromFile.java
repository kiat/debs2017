package edu.rice.data;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Scanner;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;

public class ReadingFromFile {

	public static final String PREFIXES = "PREFIX iotcore: <http://www.agtinternational.com/ontologies/IoTCore#>  "
			+ "PREFIX ar:   <http://www.agtinternational.com/ontologies/DEBSAnalyticResults#> " + "PREFIX debs2017: <http://project-hobbit.eu/resources/debs2017>   "
			+ "PREFIX ssn: <http://purl.oclc.org/NET/ssnx/ssn#> " + "PREFIX WeidmullerExamples: <http://www.agtinternational.com/resources/WeidmullerExamples#> "
			+ "PREFIX i40: <http://www.agtinternational.com/ontologies/I4.0#>" + "PREFIX owl: <http://www.w3.org/2002/07/owl#>"
			+ "PREFIX qudt: <http://data.nasa.gov/qudt/owl/qudt#>" + "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
			+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " + "PREFIX ssn: <http://purl.oclc.org/NET/ssnx/ssn#> "
			+ "PREFIX wmm: <http://www.agtinternational.com/ontologies/WeidmullerMetadata#> " + "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> ";

	public static final String queryString = PREFIXES + " SELECT ?machine ?observedDimension  ?time ?outputLiteral WHERE { "
			+ " ?ObservationGroup rdf:type               i40:MoldingMachineObservationGroup   . " + " ?ObservationGroup i40:machine            ?machine . "
			+ " ?ObservationGroup ssn:observationResultTime ?time  . " + " ?ObservationGroup i40:contains           ?observation . "
			+ " ?observation      ssn:observedProperty   ?observedDimension ." + " ?observation      ssn:observationResult  ?output . "
			+ " ?output           ssn:hasValue           ?outputValue . " + " ?outputValue      iotcore:valueLiteral   ?outputLiteral . " + " }  ORDER BY ASC(?timeValue)";

	public static final Query query = QueryFactory.create(queryString);
	public static Model model = ModelFactory.createDefaultModel();


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
	private static final char[] Output = { 'O', 'u', 't', 'p', 'u', 't', '\0' };
	private static final char[] Value = { 'V', 'a', 'l', 'u', 'e', '\0' };
	private static final char[] Cycle = { 'C', 'y', 'c', 'l', 'e', '\0' };
	
	static int machineIndex=0; 
	static int cycleSize;
	static int dimension=0;
	static int valueIndex=0;
	static double value = 0;
	static int observationNumber=0;
	static int outIndex;
	
	
	public static void main(String[] args) {
		long startTime = 0;

		// NOW read the objects from memory
		// START OF Time calculation
		startTime = System.nanoTime();

		File file = new File("./src/main/resources/molding_machine_10M.nt");

		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line = "";
			String observationGroupTmp = "";
			String segment = "";

			while ((line = br.readLine()) != null) {
				String[] tripleParts = line.split(" ");

				if (tripleParts[0].contains("Group") && tripleParts[0].compareTo(observationGroupTmp) != 0) {

					if (segment.compareTo("") != 0) {

						// TODO: You can change the method to check the
						// performance.
						processRDFMessage(segment.getBytes());
//						processData(segment.getBytes());
					}

					segment = "";
					observationGroupTmp = tripleParts[0];
				}
				segment = segment + line + "\n";
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		// End of time calculation
		long endTime = System.nanoTime();
		double elapsedTotalTime = (endTime - startTime) / 1000000000.0;

		System.out.println("Elapsed Time " + String.format("%.9f", elapsedTotalTime));

	}

	private static void processRDFMessage(byte[] bytes) {
		
		model.removeAll();
		model.read(new ByteArrayInputStream(bytes), null, "N-TRIPLES");

		// String queryStringBackup = PREFIXES
		// +
		// " SELECT ?machine ?observedDimension  ?time    ?outputLiteral WHERE { "
		// +
		// " ?ObservationGroup rdf:type                  i40:MoldingMachineObservationGroup   . "
		// + " ?ObservationGroup i40:machine            ?machine . "
		// + " ?ObservationGroup ssn:observationResultTime ?time  . "
		// + " ?time             iotcore:valueLiteral   ?timeValue  . "
		// // + " ?ObservationGroup i40:observedCycle      ?cycles  . "
		// // + " ?cycles           iotcore:valueLiteral   ?count . "
		// + " ?ObservationGroup i40:contains           ?observation . " +
		// " ?observation      ssn:observedProperty   ?observedDimension ."
		// + " ?observation      ssn:observationResult  ?output . " +
		// " ?output           ssn:hasValue           ?outputValue . "
		// + " ?outputValue      iotcore:valueLiteral   ?outputLiteral . " +
		// " } ORDER BY ASC(?timeValue)";

		try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
			ResultSet results = qexec.execSelect();

			for (; results.hasNext();) {
				QuerySolution soln = results.nextSolution();
				// Get a result variable by name.
				RDFNode observedDimension = soln.get("observedDimension");
				RDFNode time = soln.get("time");
				RDFNode machine = soln.get("machine");

				Literal outputLiteral = soln.getLiteral("outputLiteral");
				Object myLiteralObject = outputLiteral.getValue();

				if (myLiteralObject instanceof Double)
					System.out.println(machine.asResource().getLocalName() + "," + observedDimension.asResource().getLocalName() + "," + time.asResource().getLocalName() + ","
							+ outputLiteral.getDouble());

				if (myLiteralObject instanceof Integer)
					System.out.println(machine.asResource().getLocalName() + "," + observedDimension.asResource().getLocalName() + "," + time.asResource().getLocalName() + ","
							+ outputLiteral.getInt());

				if (myLiteralObject instanceof String)
					System.out.println(machine.asResource().getLocalName() + "," + observedDimension.asResource().getLocalName() + "," + time.asResource().getLocalName() + ","
							+ outputLiteral);

			}
		}
	}

	private static void processData(byte[] bytes) {
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
		boolean isOutput = true;
		boolean isValue = true;
		boolean isCycle = true;
		
		
		

		

		int j = 0;

		// figure out what we are dealing with....
		while (chars[i] != '_') {

			if (isObservationGroup) {
				isObservationGroup = ObservationGroup[j] == chars[i];
			}

			if (isObservation) {
				isObservation = Observation[j] == chars[i];
			}

//			if (isOutput) {
//				isOutput = Output[j] == chars[i];
//			}

			if (isValue) {
				isValue = Value[j] == chars[i];
			}

//			if (isCycle) {
//				isCycle = Cycle[j] == chars[i];
//			}

			// we don't care about the others.
			if (!isObservation && !isOutput && !isValue && !isObservationGroup && !isCycle) {
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
//		observationNumber = Integer.parseUnsignedInt(new String(chars, i, j - i));

		// skip "> "
		i = j + 2;

		// we ware dealing wit an observation
		if (isObservation) {
			// here we only care about the dimension...
			if (chars[i + observationSkip1] == 'o' && chars[i + observationSkip2] == 'P') {
				i = findCharacter(chars, '_', i + observationSkip3) + 1;
				j = findCharacter(chars, '>', i);

				dimension = Integer.parseUnsignedInt(new String(chars, i, j - i));
//				System.out.println("Observation_" + observationNumber + ", dimension : " + dimension);
				return;
			}

//			if (chars[i + observationSkip1] == 'o' && chars[i + observationSkip4] == 'R') {
//				j = findCharacter(chars, '>', i + observationSkip5);
//				i += observationSkip5;
//
//				outIndex = Integer.parseUnsignedInt(new String(chars, i, j - i));
////				System.out.println("Observation_" + i + ", Output number : " + outIndex);
//
//				return;
//			}
		}

		// if we are dealing with an observation group
		if (isObservationGroup) {
			// in observation we only are interested in the machine...
			if (chars[i + machineSkip] != 'm') {
				return;
			}

			j = findCharacter(chars, '>', i + machineSkip2);
			i += machineSkip2;

			machineIndex = Integer.parseUnsignedInt(new String(chars, i, j - i));
//			System.out.println("ObservationGroup_" + i + ", Machine number :" + machineIndex);

		}

//		if (isCycle) {
//			// if it's a cycle we are only interested in the
//			if (chars[i + cycleSkip1] != 'v') {
//				return;
//			}
//
//			j = findCharacter(chars, '"', i + cycleSkip2);
//			i += cycleSkip2;
//
//			cycleSize = Integer.parseUnsignedInt(new String(chars, i, j - i));
//
////			System.out.println("Cycle_" + index + ", Cycle size : " + cycleSize);
//
//		}

//		if (isOutput) {
//			if (chars[i + outputSkip1] != 'h') {
//				return;
//			}
//
//			j = findCharacter(chars, '>', i + outputSkip2);
//			i += outputSkip2;
//
//			valueIndex = Integer.parseUnsignedInt(new String(chars, i, j - i));
//
////			System.out.println("Output_" + index + ", Value  : " + valueIndex);
//		}

		if (isValue) {
			if (chars[i + valueSkip1] != 'v') {
				return;
			}

			j = findCharacter(chars, '"', i + valueSkip2);
			i += valueSkip2;

			String myValue = new String(chars, i, j - i);
			

			if (myValue.charAt(1) != 'A'){
				
				value = Double.parseDouble(myValue);

//			System.out.println("Value_" + index + " = : " + value);
			System.out.println(machineIndex+"," +  dimension + ","+  value);
			}

		}

		
		
		
		
	}

}
