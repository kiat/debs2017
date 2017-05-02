package edu.rice.metadata;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RDFDataMgr;

import edu.rice.utils.Constants;

public class ReadConstantsFromSystemTTL {

	public static String PREFIXES = "PREFIX iotcore: <http://www.agtinternational.com/ontologies/IoTCore#>  "
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

	public static String queryString = PREFIXES + " SELECT * where { "
			+ " ?s <http://www.debs2017.org/gc/maxClusterIterations> ?maxClusterIterations . "
			+ " ?s <http://www.debs2017.org/gc/transitionsCount> ?transitionsCount . "
			+ " ?s <http://www.debs2017.org/gc/windowSize>  ?windowSize . "
			+ " ?s <http://www.debs2017.org/gc/probabilityThreshold>  ?probabilityThreshold . } ";
	
	

	
	
	public static void readSystemTTL(){
		Query query = QueryFactory.create(queryString);

		Model model = RDFDataMgr.loadModel("./system.ttl") ;
		int maxClusterIterations=50;
		int transitionsCount=5;
		int windowSize=10;
		double probabilityThreshold=0.005;
		
		
			try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
				
				ResultSet results = qexec.execSelect();

				for (; results.hasNext();) {
					QuerySolution soln = results.nextSolution();

//					System.out.println(soln.toString());
					// Get a result variable by name.
					Literal maxClusterIterationsRDFNode = soln.getLiteral("maxClusterIterations");
					Literal transitionsCountRDFNode = soln.getLiteral("transitionsCount");
					Literal windowSizeRDFNode = soln.getLiteral("windowSize");
					Literal probabilityThresholdLiteral = soln.getLiteral("probabilityThreshold");

					maxClusterIterations = maxClusterIterationsRDFNode.getInt();
					transitionsCount = transitionsCountRDFNode.getInt();
					windowSize = windowSizeRDFNode.getInt();

					Object probabilityThresholdValue = probabilityThresholdLiteral.getValue();
					
					if (probabilityThresholdValue instanceof Double) {
						probabilityThreshold = probabilityThresholdLiteral.getDouble();
					}
				}
			

		} catch (Exception e) {
			System.err.println("Exception in ReadConstantsFromSystemTTL.readSystemTTL : ");
			e.printStackTrace();
		}
			
			
			Constants.MAX_CLUSTERING_ITERATION=maxClusterIterations;
			Constants.WINDOW_SIZE=windowSize;
			Constants.SMALLER_WINDOW=transitionsCount;
			Constants.THRESHOLD=probabilityThreshold;
			

	
	} 
	
	
	
	
	
	public static void main(String[] args) {

			ReadConstantsFromSystemTTL.readSystemTTL();

			System.out.println(Constants.MAX_CLUSTERING_ITERATION);
			System.out.println(Constants.WINDOW_SIZE);
			System.out.println(Constants.SMALLER_WINDOW);
			System.out.println(Constants.THRESHOLD);

			
	}
}