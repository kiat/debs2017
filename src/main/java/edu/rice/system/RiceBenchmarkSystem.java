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

class RiceBenchmarkSystem extends DebsParrotBenchmarkSystem {

    private static final String PREFIXES =
            "PREFIX iotcore: <http://www.agtinternational.com/ontologies/IoTCore#>  "
                    + "PREFIX ar:   <http://www.agtinternational.com/ontologies/DEBSAnalyticResults#> "
                    + "PREFIX debs2017: <http://project-hobbit.eu/resources/debs2017>   "
                    + "PREFIX ssn: <http://purl.oclc.org/NET/ssnx/ssn#> "
                    + "PREFIX WeidmullerExamples: <http://www.agtinternational.com/resources/WeidmullerExamples#> "
                    + "PREFIX i40: <http://www.agtinternational.com/ontologies/I4.0#>"
                    + "PREFIX owl: <http://www.w3.org/2002/07/owl#>"
                    + "PREFIX qudt: <http://data.nasa.gov/qudt/owl/qudt#>"
                    + "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
                    + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
                    + "PREFIX ssn: <http://purl.oclc.org/NET/ssnx/ssn#> "
                    + "PREFIX wmm: <http://www.agtinternational.com/ontologies/WeidmullerMetadata#> "
                    + "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> ";

    
	public static final String queryString = PREFIXES + " SELECT ?machine ?observedDimension  ?time ?outputLiteral WHERE { "
			+ " ?ObservationGroup rdf:type               i40:MoldingMachineObservationGroup   . " + " ?ObservationGroup i40:machine            ?machine . "
			+ " ?ObservationGroup ssn:observationResultTime ?time  . " + " ?ObservationGroup i40:contains           ?observation . "
			+ " ?observation      ssn:observedProperty   ?observedDimension ." + " ?observation      ssn:observationResult  ?output . "
			+ " ?output           ssn:hasValue           ?outputValue . " + " ?outputValue      iotcore:valueLiteral   ?outputLiteral . " + " }  ORDER BY ASC(?timeValue)";
	









	
    @Override
    protected void processData(byte[] bytes) {

    	
		int machineNr=0;
		int dimensionNr=0;
		int timestampNr=0;
		double value=0;
		
		
    	// TODO: maybe we can create one Model instance and reuse it each time when we get a message. 
    	Model model = ModelFactory.createDefaultModel();

    	// This is important to have correct Formating name, in our case the RDF message is formated in N-Triples format so that it can be streamed. 
    	model.read(new ByteArrayInputStream(bytes), null, "N-TRIPLES") ;

		
		// This is the SPARQL Query
		String queryString = PREFIXES
				+ " SELECT ?machine ?observedDimension  ?time ?outputLiteral WHERE { "
				+ " ?ObservationGroup rdf:type               i40:MoldingMachineObservationGroup   . "
				+ " ?ObservationGroup i40:machine            ?machine . "
				+ " ?ObservationGroup ssn:observationResultTime ?time  . "
				+ " ?ObservationGroup i40:contains           ?observation . " 
				+ " ?observation      ssn:observedProperty   ?observedDimension ."
				+ " ?observation      ssn:observationResult  ?output . " 
				+ " ?output           ssn:hasValue           ?outputValue . "
				+ " ?outputValue      iotcore:valueLiteral   ?outputLiteral . " 
				+ " }";


		// Here we create a query object from the string.
		// TODO: We need to create only one time this object, not on each arriving message. 
		Query query = QueryFactory.create(queryString);

		
		// Then we execute the query on the RDF model and get the results.  
		try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
			ResultSet results = qexec.execSelect();

			
			// After having the results, we go through all results 
			for (; results.hasNext();) {				
				QuerySolution soln = results.nextSolution();

				// We get the results by using the name of variables that used in the SPARQK query. 
				RDFNode observedDimension = soln.get("observedDimension") ; // Get a result variable by name.
				RDFNode time = soln.get("time") ; // Get a result variable by name.
				RDFNode machine = soln.get("machine") ; // Get a result variable by name.

				// We have only one Literanl value and that is the actual value that we need to use for our process. 
				Literal outputLiteral = soln.getLiteral("outputLiteral") ; // Get a result variable - must be a literal
				
				// We need to know the data type of it to be able to get the actual value.  				
				Object myLiteralObject=outputLiteral.getValue();

				
				// Then use different methods for different value types. 
				// TODO: we have only Double values. 
				
				machineNr =  Integer.parseInt(machine.asResource().getLocalName().substring(8));
				dimensionNr = Integer.parseInt(observedDimension.asResource().getLocalName().substring(1).split("_")[1]);
				timestampNr = Integer.parseInt(time.asResource().getLocalName().split("_")[1]);
				
				
				if (myLiteralObject instanceof Double){
					value=outputLiteral.getDouble();
					System.out.println(machineNr + "," + dimensionNr + "," + timestampNr + "," + value);
				}
			}
		}
			
    }
}
