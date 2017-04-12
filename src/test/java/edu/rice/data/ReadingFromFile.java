package edu.rice.data;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.Scanner;

import edu.rice.system.Controller;
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

	public static void main(String[] args) throws ParseException {

	    // open the file with the data
		File file = new File("./src/main/resources/molding_machine_10M.nt");

		// this will hold the data
        LinkedList<byte[]> data = new LinkedList<>();

        // read the file and process the data...
		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line;
			String observationGroupTmp = "";
			String segment = "";

			while ((line = br.readLine()) != null) {
				String[] tripleParts = line.split(" ");

				if (tripleParts[0].contains("Group") && tripleParts[0].compareTo(observationGroupTmp) != 0) {

					if (segment.compareTo("") != 0) {
                        data.addLast(segment.getBytes());
					}

					segment = "";
					observationGroupTmp = tripleParts[0];
				}
				segment = segment + line + "\n";
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

        long startTime = System.nanoTime();

		for(byte[] b : data ) {
//		    processRDFMessage(b);
            RDFParser.processData(b);
        }

		// End of time calculation
		long endTime = System.nanoTime();
		double elapsedTotalTime = (endTime - startTime) / 1000000000.0;

		System.out.println("Elapsed Time " + String.format("%.9f", elapsedTotalTime));

	}

    private static void processRDFMessage(byte[] bytes) {

        int machineNr = 0;
        int dimensionNr = 0;
        int timestampNr = 0;
        double value = 0;


//		KMeans myKmeans= new KMeans();

        model.removeAll();
        model.read(new ByteArrayInputStream(bytes), null, "N-TRIPLES");

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

                machineNr = Integer.parseInt(machine.asResource().getLocalName().substring(8));
                dimensionNr = Integer.parseInt(observedDimension.asResource().getLocalName().substring(1).split("_")[1]);
                timestampNr = Integer.parseInt(time.asResource().getLocalName().split("_")[1]);



                if (myLiteralObject instanceof Double) {
                    value = outputLiteral.getDouble();
//					System.out.println(machineNr+ "," + dimensionNr + "," + timestampNr + "," + value);

                    Controller. getInstance().pushData(machineNr, dimensionNr, timestampNr, value);

                }

            }
        }
    }

}
