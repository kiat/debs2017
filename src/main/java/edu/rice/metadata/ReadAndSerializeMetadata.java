package edu.rice.metadata;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.riot.RDFDataMgr;

public class ReadAndSerializeMetadata {

	// RDF prefixes used in the debs2017 grand challenge metadata.
	public static final String PREFIXES = "PREFIX iotcore: <http://www.agtinternational.com/ontologies/IoTCore#>  "
			+ "PREFIX ar:   <http://www.agtinternational.com/ontologies/DEBSAnalyticResults#> " + "PREFIX debs2017: <http://project-hobbit.eu/resources/debs2017>   "
			+ "PREFIX ssn: <http://purl.oclc.org/NET/ssnx/ssn#> " + "PREFIX WeidmullerExamples: <http://www.agtinternational.com/resources/WeidmullerExamples#> "
			+ "PREFIX i40: <http://www.agtinternational.com/ontologies/I4.0#>" + "PREFIX owl: <http://www.w3.org/2002/07/owl#>"
			+ "PREFIX qudt: <http://data.nasa.gov/qudt/owl/qudt#>" + "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
			+ "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> " + "PREFIX ssn: <http://purl.oclc.org/NET/ssnx/ssn#> "
			+ "PREFIX wmm: <http://www.agtinternational.com/ontologies/WeidmullerMetadata#> " + "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> ";

	public static HashMap<Integer, int[]> clusterNo = new HashMap<Integer, int[]>();
	public static HashMap<Integer, double[]> threshhold = new HashMap<Integer, double[]>();

	public static void main(String[] args) {

		ReadAndSerializeMetadata myReader = new ReadAndSerializeMetadata();

//		myReader.readMetaData("./1000molding_machine.metadata.nt");
//		myReader.readMetaData("./10molding_machine_5000dp.metadata.nt");
//		myReader.readMetaData("./molding_machine_5000dp.metadata.nt");
		myReader.readMetaData("./molding_machine_10M.metadata.nt");
		

		
//		myReader.writeMetadataToFile("1000molding_machine.metadata.data");
//		myReader.writeMetadataToFile("10molding_machine_5000dp.metadata.data");
//		myReader.writeMetadataToFile("molding_machine_5000dp.metadata.data");
		myReader.writeMetadataToFile("metadata_10M.data");
		
	}

	/**
	 * Binary Serialization of this object using ByteBuffer
	 * 
	 * @return
	 */
	public void writeMetadataToFile(String outPutFileName) {
		File fileOutput = new File(outPutFileName);

		try {
			FileChannel channel = new FileOutputStream(fileOutput, false).getChannel();

			System.out.println("No of Entries: " + clusterNo.size());
			
			// write first the total size of the map.
			ByteBuffer byteBufferSize = ByteBuffer.allocate(4);
			byteBufferSize.putInt(clusterNo.size());
			byteBufferSize.flip();

			channel.write(byteBufferSize);

			Iterator<Entry<Integer, int[]>> it = clusterNo.entrySet().iterator();

			while (it.hasNext()) {
				Map.Entry<Integer, int[]> pair = (Map.Entry<Integer, int[]>) it.next();

				int[] m_Array = (int[]) pair.getValue();
				ByteBuffer byteBuffer = ByteBuffer.allocate(4+55*8);
				byteBuffer.putInt(pair.getKey());
				// write it to file.
				
				// this counter is always 55, because 55 sensors are statefull.
				for (int i = 0; i < m_Array.length; i++) {
					if (m_Array[i] != 0) {
						byteBuffer.putInt(i);
						byteBuffer.putInt(m_Array[i]);
					}
				}

				byteBuffer.flip();
				channel.write(byteBuffer);
				
				
				// System.out.println(pair.getKey() + " = " + arrayCounter);
				it.remove(); // avoids a ConcurrentModificationException
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return;
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
	public int getClusterNr(int machineNr, int dimensionNr) {
		if (clusterNo.containsKey(machineNr))
			return clusterNo.get(machineNr)[dimensionNr];
		else
			return 0;
	}

	// getter method for thresholds
	public double getThreshold(int machineNr, int dimensionNr) {
		if (threshhold.containsKey(machineNr))
			return threshhold.get(machineNr)[dimensionNr];
		else
			return 0.0;
	}

	// This method read a metadata file and fills it in RAM
	public void readMetaData(String fileName) {

		Model model = RDFDataMgr.loadModel(fileName);

		String queryString = PREFIXES + "Select  * where { " + "?machine  rdf:type  i40:MachineType . " + "?machine  ssn:hasProperty  ?numberOfClusterPerDim . "
				+ "?numberOfClusterPerDim wmm:hasNumberOfClusters  ?numberOfClusters ." + "?threshold wmm:isThresholdForProperty ?numberOfClusterPerDim  ."
				+ "?threshold iotcore:valueLiteral ?thresholdValue  .  " + "?numberOfClusterPerDim rdf:type wmm:StatefulProperty ." + " } ORDER BY ASC(?machine)";

		Query query = QueryFactory.create(queryString);

		try (QueryExecution qexec = QueryExecutionFactory.create(query, model)) {
			ResultSet results = qexec.execSelect();

			for (; results.hasNext();) {
				QuerySolution soln = results.nextSolution();

				// this prints all of the results
				// System.out.println(soln.toString());

				// Get a result variable by name.
				RDFNode machine = soln.get("machine"); 
				 // Get a result variable by name.
				RDFNode dimension = soln.get("numberOfClusterPerDim");

				int numberOfClusters = soln.getLiteral("numberOfClusters").getInt();
				int machineNr = Integer.parseInt(machine.asResource().getLocalName().substring(13));
				int dimensionNr = Integer.parseInt(dimension.asResource().getLocalName().substring(1).split("_")[1]);
				double threshold = soln.getLiteral("thresholdValue").getDouble();

				// check if we see this machine for the first time?
				if (!machineIsAdded(machineNr))
					addMachine(machineNr);

				clusterNo.get(machineNr)[dimensionNr] = numberOfClusters;
				threshhold.get(machineNr)[dimensionNr] = threshold;

				// System.out.println(machineNr + "," + dimensionNr + "," +
				// numberOfClusters + "," + threshold);

			}
		}

		model.close();
	}

}
