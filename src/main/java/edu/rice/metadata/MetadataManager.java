package edu.rice.metadata;

import java.util.HashMap;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.riot.RDFDataMgr;

public class MetadataManager {

	String queryString = PREFIXES + "Select  * where { " + "?machine  rdf:type  i40:MachineType . " + "?machine  ssn:hasProperty  ?numberOfClusterPerDim . "
			+ "?numberOfClusterPerDim wmm:hasNumberOfClusters  ?numberOfClusters ." + "?threshold wmm:isThresholdForProperty ?numberOfClusterPerDim  ."
			+ "?threshold iotcore:valueLiteral ?thresholdValue  .  " + "?numberOfClusterPerDim rdf:type wmm:StatefulProperty ." + "  } ";

	public static final String PREFIXES = "PREFIX iotcore: <http://www.agtinternational.com/ontologies/IoTCore#>  "
			+ "PREFIX ar:   <http://www.agtinternational.com/ontologies/DEBSAnalyticResults#> "
			+ "PREFIX debs2017: <http://project-hobbit.eu/resources/debs2017>   " + "PREFIX ssn: <http://purl.oclc.org/NET/ssnx/ssn#> "
			+ "PREFIX WeidmullerExamples: <http://www.agtinternational.com/resources/WeidmullerExamples#> "
			+ "PREFIX i40: <http://www.agtinternational.com/ontologies/I4.0#>" + "PREFIX owl: <http://www.w3.org/2002/07/owl#>"
			+ "PREFIX qudt: <http://data.nasa.gov/qudt/owl/qudt#>" + "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
			+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " + "PREFIX ssn: <http://purl.oclc.org/NET/ssnx/ssn#> "
			+ "PREFIX wmm: <http://www.agtinternational.com/ontologies/WeidmullerMetadata#> " + "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> ";

	public static HashMap<Integer, int[]> clusterNo;
	public static HashMap<Integer, int[]> threshhold;

	public MetadataManager() {
		clusterNo = new HashMap<Integer, int[]>();
		threshhold = new HashMap<Integer, int[]>();
	}

	public void addMachine(int machineNr) {
		int[] clusterNumbers = new int[121];
		int[] threshhold = new int[121];
		clusterNo.put(machineNr, clusterNumbers);
		clusterNo.put(machineNr, threshhold);
	}

	public boolean machineIsAdded(int machineNr) {

		if (clusterNo.containsKey(machineNr))
			return true;
		else
			return false;

	}

	public void readMetaData(String fileName) {

		Model model = RDFDataMgr.loadModel(fileName);

		Query query = QueryFactory.create(queryString);

		try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
			ResultSet results = qexec.execSelect();

			System.out.println("Number of Results " + results.hasNext());

			for (; results.hasNext();) {
				QuerySolution soln = results.nextSolution();

				// this prints all of the results
				// System.out.println(soln.toString());

				RDFNode machine = soln.get("machine"); // Get a result variable by name.
				RDFNode dimension = soln.get("numberOfClusterPerDim"); // Get a result variable by name.


				int numberOfClusters = soln.getLiteral("numberOfClusters").getInt();
				int machineNr = Integer.parseInt(machine.asResource().getLocalName().substring(13));
				int dimensionNr = Integer.parseInt(dimension.asResource().getLocalName().substring(1).split("_")[1]);
				double threshold = soln.getLiteral("thresholdValue").getDouble();
				
//				if()
				
				
				
				System.out.println(machineNr + "," + dimensionNr + "," + numberOfClusters + "," + threshold);

			}
		}
	}

}