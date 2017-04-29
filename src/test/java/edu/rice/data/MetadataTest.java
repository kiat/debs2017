package edu.rice.data;

import edu.rice.metadata.MetadataManager;

public class MetadataTest {

	public static void main(String[] args) {
		MetadataManager.getInstance().readMetaData("./10molding_machine_5000dp.metadata.data");

		for (int i = 0; i < 1000; i++) {
			for (int j = 0; j < 120; j++) {
			System.out.println(i+"," + j +"="+ MetadataManager.getInstance().getClusterNr(i, j) );
			}
			
		}
		
		
	}
}