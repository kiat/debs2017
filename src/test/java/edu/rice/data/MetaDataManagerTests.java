package edu.rice.data;

import static org.junit.Assert.*;

import org.junit.Test;

import edu.rice.metadata.MetadataManager;

public class MetaDataManagerTests {

	@Test
	public void test() {
		
		 MetadataManager myMetadataManager = new MetadataManager();
		 myMetadataManager.readMetaData("./src/main/resources/molding_machine_10M.metadata.nt");
	}
}