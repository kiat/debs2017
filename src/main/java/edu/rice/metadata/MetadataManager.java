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

	// RDF prefixes used in the debs2017 grand challenge metadata.  
	public static final String PREFIXES = 
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
	


	public static HashMap<Integer, int[]> clusterNo;
	public static HashMap<Integer, double[]> threshhold;

	public MetadataManager() {
		clusterNo = new HashMap<Integer, int[]>();
		threshhold = new HashMap<Integer, double[]>();
	}

	
	
    static class SingletonHolder {
    	static 	final MetadataManager instance = new MetadataManager();
    }
    
    
    public static MetadataManager getInstance() {
		return SingletonHolder.instance;

    }
	
	
	
	
	
	public void addMachine(int machineNr) {
		int[] clusterNumbers = new int[121];
		double[] threshholds = new double[121];
		clusterNo.put(machineNr, clusterNumbers);
		threshhold.put(machineNr, threshholds);
	}


	
	
	public boolean machineIsAdded(int machineNr) {
		if (clusterNo.containsKey(machineNr))
			return true;
		else
			return false;
	}


	// getter method for cluster numbers 
	public int getClusterNr(int machineNr, int dimensionNr ){
		if (clusterNo.containsKey(machineNr))
			return clusterNo.get(machineNr)[dimensionNr];
		else
			return 0;
	}
	
	
	// getter method for thresholds  
	public double getThreshold(int machineNr, int dimensionNr ){
		if (threshhold.containsKey(machineNr))
			return threshhold.get(machineNr)[dimensionNr];
		else
			return 0.0;
	}
	
	
	// This method read a metadata file and fills it in RAM
	public void readMetaData(String fileName) {

		Model model = RDFDataMgr.loadModel(fileName);

		
        String  queryString = PREFIXES 
				+ "Select  * where { " 
		        + "?machine  rdf:type  i40:MachineType . " 
				+ "?machine  ssn:hasProperty  ?numberOfClusterPerDim . "
				+ "?numberOfClusterPerDim wmm:hasNumberOfClusters  ?numberOfClusters ." 
				+ "?threshold wmm:isThresholdForProperty ?numberOfClusterPerDim  ."
				+ "?threshold iotcore:valueLiteral ?thresholdValue  .  " 
				+ "?numberOfClusterPerDim rdf:type wmm:StatefulProperty ." 
				+ " } ORDER BY ASC(?machine)";
		
		
		Query query = QueryFactory.create(queryString);
		
		System.out.println(queryString);

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
				
				// check if we see this machine for the first time?
				if(!machineIsAdded(machineNr))
					addMachine(machineNr);
				
					
				clusterNo.get(machineNr)[dimensionNr]=numberOfClusters;
				threshhold.get(machineNr)[dimensionNr]=threshold;
				
				System.out.println(machineNr + "," + dimensionNr + "," + numberOfClusters + "," + threshold);

			}
		}
		
		
		model.close();
	}
	

}