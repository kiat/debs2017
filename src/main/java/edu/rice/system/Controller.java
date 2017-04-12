package edu.rice.system;

import java.util.HashMap;
import java.util.LinkedList;

import edu.rice.metadata.MetadataManager;
import edu.rice.utils.Constants;

public class Controller {

	// Window for each machine-dimension - First access the machine and then an
	// Array of ArrayList.
	static HashMap<Integer, LinkedList<Double>> windowsMap=new HashMap<Integer, LinkedList<Double>>();;

	private Controller() {
		// Reads the metadata and have it ready for use.
		MetadataManager.getInstance().readMetaData("./src/main/resources/molding_machine_10M.metadata.nt");
	}

	static class ControllerHolder {
		static final Controller instance = new Controller();
	}

	public static Controller getInstance() {
		return ControllerHolder.instance;
	}

	// we call each time this method
	public void pushData(int machineNr, int dimensionNr, int timestampNr, double value) {

		int machine_Dimension_ID = machineNr * 10000 + dimensionNr;

		// First check if we see this machine for the first time
		if (!windowsMap.containsKey(machine_Dimension_ID)) {

			LinkedList<Double> d_windows = new LinkedList<Double>();
			// add the first value;
			d_windows.add(value);
			// put the array list in to the map
			windowsMap.put(machine_Dimension_ID, d_windows);

		} else {
			// if it is not the first time.
			LinkedList<Double> tmp = windowsMap.get(machine_Dimension_ID);
			tmp.add(value);
			// put it back 
			windowsMap.put(machine_Dimension_ID, tmp);

			// then check if the window is filled up for this dimension
			if (windowsMap.get(machine_Dimension_ID).size() == Constants.Window_Size) {
				
				LinkedList<Double> m_window = windowsMap.get(machine_Dimension_ID);
				
				System.out.println("Machine_" + machineNr +"_"+dimensionNr+"  Window:"+m_window.toString());
				
				m_window.remove();
				// put it back 
				windowsMap.put(machine_Dimension_ID, tmp);
			}

			// Then if we see this machine before, then check if we saw this
			// dimension before.
			// if yes, check if the window is filled up and if yes then just send
			// the new data value, if no add the new data item into the list.

		}
	}
	
}