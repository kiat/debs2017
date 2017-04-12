package edu.rice.system;

import java.util.ArrayList;

import com.github.andrewoma.dexx.collection.HashMap;

import edu.rice.metadata.MetadataManager;

public class Controller {

	// This HashMap keeps track of all seen Machines and Dimensions so far.
	HashMap<Integer, boolean[]> machineDimensions;

	// This HashMap keeps track of the started Threads for Machine+Dimension .
	HashMap<Integer, Thread[]> machineDimTheads;

	// Window for each machine-dimension - First access the machine and then an
	// Array of ArrayList.
	HashMap<Integer, ArrayList<Double>[]> windows;

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

	// we call each time this method
	public void pushData(int machineNr, int dimensionNr, int timestampNr, double value) {

		if (!machineDimensions.containsKey(machineNr)) {
			HashMap<Integer, boolean[]> machineDimensions = new HashMap<Integer, boolean[]>();
			boolean[] seenDimensions=new boolean[121];
			for (int i = 0; i < seenDimensions.length; i++) {
				seenDimensions[i]=false;
			}

			
			
			machineDimensions.put(machineNr, seenDimensions);
			
			
		}

		// First check if we saw before this machine.

		// If yes, then check if the window is filled up for this dimension of
		// this machine

		// If not we should create 3 HashMap for this machine

		// Then if we see this machine before, then check if we saw this
		// dimension before.
		// if yes, check if the window is filled up and if yes then just send
		// the new data value, if no add the new data item into the list.

	}

	// just for testing.
	public static void main(String[] args) {

	}

}