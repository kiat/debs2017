package edu.rice.data;

import static org.junit.Assert.*;

import org.junit.Test;

import edu.rice.metadata.MetadataManager;

public class MetaDataManagerTests {

	@Test
	public void test() {
		
		 MetadataManager myMetadataManager = new MetadataManager();
		 myMetadataManager.readMetaData("./src/main/resources/molding_machine_10M.metadata.nt");
		 
		 assertEquals(myMetadataManager.getClusterNr(59, 6), 10);
		 assertEquals(myMetadataManager.getClusterNr(59, 66), 4);
		 
		 
		 
		 assertEquals(myMetadataManager.getThreshold(59, 6), 0.005, 0);
		 assertEquals(myMetadataManager.getThreshold(59, 66), 0.005, 0);
	}
}