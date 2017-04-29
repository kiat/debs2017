package edu.rice.metadata;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

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
	// public static final String PREFIXES =
	// "PREFIX iotcore: <http://www.agtinternational.com/ontologies/IoTCore#>  "
	// +
	// "PREFIX ar:   <http://www.agtinternational.com/ontologies/DEBSAnalyticResults#> "
	// + "PREFIX debs2017: <http://project-hobbit.eu/resources/debs2017>   "
	// + "PREFIX ssn: <http://purl.oclc.org/NET/ssnx/ssn#> "
	// +
	// "PREFIX WeidmullerExamples: <http://www.agtinternational.com/resources/WeidmullerExamples#> "
	// + "PREFIX i40: <http://www.agtinternational.com/ontologies/I4.0#>"
	// + "PREFIX owl: <http://www.w3.org/2002/07/owl#>"
	// + "PREFIX qudt: <http://data.nasa.gov/qudt/owl/qudt#>"
	// + "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
	// + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
	// + "PREFIX ssn: <http://purl.oclc.org/NET/ssnx/ssn#> "
	// +
	// "PREFIX wmm: <http://www.agtinternational.com/ontologies/WeidmullerMetadata#> "
	// + "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#> ";

	public static HashMap<Integer, int[]> clusterNo;
	public static HashMap<Integer, double[]> threshhold;

	private MetadataManager() {
		clusterNo = new HashMap<Integer, int[]>();
		threshhold = new HashMap<Integer, double[]>();
	}

	static class SingletonHolder {
		static final MetadataManager instance = new MetadataManager();
	}

	public static MetadataManager getInstance() {
		return SingletonHolder.instance;
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

	public void readMetaData(String fileName) {

		Path path = Paths.get(fileName);
		FileChannel fileChannel;
		try {
			fileChannel = FileChannel.open(path);

			// fileChannel.position(0);

			ByteBuffer byteBufferSizeOfLengths = ByteBuffer.allocateDirect(4);
			fileChannel.read(byteBufferSizeOfLengths);
			byteBufferSizeOfLengths.flip();

			int totalObjectNumbers = byteBufferSizeOfLengths.getInt(); // 4

			if (totalObjectNumbers == 0) {
				System.err.println("Error this should not happen, metadata is wrong");
			}

			// 55 is hardcoded. this is the number of values in sparse index.
			// Number of statefull dimensions of each machine.
			for (int i = 0; i < totalObjectNumbers; i++) {
				ByteBuffer byteBuffer = ByteBuffer.allocate(4 + 55 * 8);
				byteBuffer.clear();
				fileChannel.read(byteBuffer);
				byteBuffer.flip();

				int key = byteBuffer.getInt();

				int[] values = new int[121]; // this is a 121 dense index.
				for (int j = 0; j < 55; j++) {
					int index = byteBuffer.getInt();
					int value = byteBuffer.getInt();
					values[index] = value;
				}

				clusterNo.put(key, values);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		MetadataManager.getInstance().readMetaData("1000molding_machine.metadata.data");

		Iterator it = clusterNo.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			
			int [] clusterNumbers=(int[]) pair.getValue(); 
			
			for (int i = 0; i < clusterNumbers.length; i++) {
				if(clusterNumbers[i]>10)
				System.out.println("MachineNr="+pair.getKey() + ", Dimension=" + i + ", NoOfClusters="+clusterNumbers[i]);
			}			
			
			it.remove(); 
		}
		

//		System.out.println(MetadataManager.getInstance().getClusterNr(59, 106));
	}

}