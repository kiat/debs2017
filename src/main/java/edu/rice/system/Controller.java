package edu.rice.system;

import com.github.andrewoma.dexx.collection.HashMap;

import edu.rice.metadata.MetadataManager;

public class Controller {

	// This HashMap keeps track of all seen Machines and Dimensions so far. 
	HashMap<Integer, boolean[]>  machineDimensions;
	
	
	
	private Controller() {
		// Reads the metadata and have it ready for use.
		MetadataManager.getInstance().readMetaData("./src/main/resources/molding_machine_10M.metadata.nt");

	}

	static class SingletonHolder {
		static final Controller instance = new Controller();
	}

	public static Controller getInstance() {
		return SingletonHolder.instance;
	}


	public void pushData(){
		
		
		
	}
	
	
	
	
	
	// just for testing.
	public static void main(String[] args) {

	}

}