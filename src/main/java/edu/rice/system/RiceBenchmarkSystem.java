package edu.rice.system;

import java.io.ByteArrayInputStream;

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

import edu.rice.system.DebsParrotBenchmarkSystem;

public class RiceBenchmarkSystem extends DebsParrotBenchmarkSystem {

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
	
	
	private static RiceBenchmarkSystem instance;
	
	protected RiceBenchmarkSystem() throws Exception {
		super();
	    this.init();
	}
	
	public static RiceBenchmarkSystem getInstance() throws Exception {
		if(instance == null) {
			instance = new RiceBenchmarkSystem();
		}
		
		return instance;
	}
	
	
	
    @Override
    protected void processData(byte[] bytes) {

		int machineNr = 0;
		int dimensionNr = 0;
		int timestampNr = 0;
		double value = 0;

		
		
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
					
					
//					RiceBenchmarkSystem.getInstance().send(bytes);
//					RiceBenchmarkSystem.getInstance().send("");
					
				}

			}
		}
			
    }
}
