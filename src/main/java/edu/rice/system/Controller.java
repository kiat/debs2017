package edu.rice.system;

import java.util.HashMap;
import java.util.LinkedList;

import edu.rice.kmeans.CircularQueue;
import edu.rice.kmeans.KMeans;
import edu.rice.kmeans.KMeansMultiThread;
import edu.rice.metadata.MetadataManager;
import edu.rice.output.OutputGenerator;
import edu.rice.utils.Constants;

public class Controller {

	// Window for each machine-dimension - First access the machine and then an
	// Array of ArrayList.
	static HashMap<Integer, CircularQueue> windowsMap = new HashMap<Integer, CircularQueue>();
	static HashMap<Integer, LinkedList<Integer>> timestamps = new HashMap<Integer, LinkedList<Integer>>();

	static int counter = 1;

	private Controller() {
	}

	static class ControllerHolder {
		static final Controller instance = new Controller();
	}

	public static Controller getInstance() {
		return ControllerHolder.instance;
	}

	// we call each time this method
	public synchronized void pushData(int machineNr, int dimensionNr, int timestampNr, double value) {

		// if this is not a statefull dimension then return
		if (MetadataManager.getInstance().getClusterNr(machineNr, dimensionNr) == 0)
			return;
		//
		// if(machineNr==8 && dimensionNr==83)
		// System.out.println(machineNr +","+ dimensionNr+","+ timestampNr +","+
		// value );

		int machine_Dimension_ID = machineNr * 100000 + dimensionNr;

		// First check if we see this machine_dimension for the first time
		if (!windowsMap.containsKey(machine_Dimension_ID)) {

			CircularQueue d_windows = new CircularQueue(Constants.WINDOW_SIZE);
			LinkedList<Integer> tmp_timestamp = new LinkedList<Integer>();

			// add the first value;
			d_windows.insert(value);
			tmp_timestamp.add((Integer) timestampNr);

			// put the array list in to the map
			windowsMap.put(machine_Dimension_ID, d_windows);
			timestamps.put(machine_Dimension_ID, tmp_timestamp);

		} else {
			// if it is not the first time.
			CircularQueue tmp = windowsMap.get(machine_Dimension_ID);
			tmp.insert(value);

			LinkedList<Integer> tmp_timestamp = timestamps.get(machine_Dimension_ID);
			tmp_timestamp.add((Integer) timestampNr);

			// put it back
			windowsMap.put(machine_Dimension_ID, tmp);
			timestamps.put(machine_Dimension_ID, tmp_timestamp);

			// then check if the window is filled up for this dimension
			if (windowsMap.get(machine_Dimension_ID).size() == Constants.WINDOW_SIZE) {

				CircularQueue m_window = windowsMap.get(machine_Dimension_ID);
				CircularQueue coppiedWindow = new  CircularQueue(Constants.WINDOW_SIZE);
				
				for(int i = 0; i < m_window.size(); ++i) {
					coppiedWindow.insert(m_window.get(i).getX());
				}
				
				LinkedList<Integer> tmp_timestamp_ifFull = timestamps.get(machine_Dimension_ID);

				int numberOfClusters = MetadataManager.getInstance().getClusterNr(machineNr, dimensionNr);

//				KMeansMultiThread kmeans = new KMeansMultiThread(machineNr, dimensionNr, numberOfClusters, coppiedWindow, Constants.THRESHOLD, (int) tmp_timestamp_ifFull.get(Constants.WINDOW_SIZE - Constants.SMALLER_WINDOW - 1));
//				kmeans.start();
				
				// then do the Kmeans and Anomaly Detection.
				KMeans singleKMeans = new KMeans();
				boolean hasAnomalies = singleKMeans.performAllCalculation(numberOfClusters, m_window, Constants.THRESHOLD);

				if (hasAnomalies) {
					double finalThreshold = singleKMeans.getThreshold();

//					if(OutputGenerator.anomalyCounter>42){
//						String output = OutputGenerator.anomalyCounter + "," + machineNr + "," + dimensionNr + "," + numberOfClusters + ",  " + finalThreshold + " ,  " + m_window.toString() + " Timestamps " +  (int) tmp_timestamp_ifFull.get(Constants.WINDOW_SIZE - Constants.SMALLER_WINDOW - 1);
//						System.out.println(output);
//					}
					
					OutputGenerator.outputAnomaly(machineNr, dimensionNr, finalThreshold, (int) tmp_timestamp_ifFull.get(Constants.WINDOW_SIZE - Constants.SMALLER_WINDOW - 1));

				}

				// FIFO remove
				m_window.remove();
				tmp_timestamp_ifFull.remove();

				// put it back
				windowsMap.put(machine_Dimension_ID, tmp);
				timestamps.put(machine_Dimension_ID, tmp_timestamp_ifFull);
			}

		}
	}

}