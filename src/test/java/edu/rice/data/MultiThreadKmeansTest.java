package edu.rice.data;

import edu.rice.kmeans.CircularQueue;
import edu.rice.kmeans.KMeans;
import edu.rice.kmeans.KMeansMultiThread;
import edu.rice.utils.Constants;

public class MultiThreadKmeansTest {

	public static void main(String[] args) {

		CircularQueue m_window = new CircularQueue(Constants.WINDOW_SIZE);
		// 2.97 3.00 2.94 2.89 3.02 2.95 2.94 3.05 3.05 2.89
		// -0.00 0.00 0.01 -0.00 -0.00 -0.05 -0.04 0.02 -0.02 -0.01
		double[] data = { -0.00, 0.00, 0.01, -0.00, -0.00, -0.05, -0.04, 0.02, -0.02, -0.01 };

		for (int i = 0; i < data.length; i++) {
			m_window.insert(data[i]);
		}

//		data[0] += 0.001;
		KMeansMultiThread kmeans = new KMeansMultiThread(1, 1, 3, m_window, 0.005, 1);
		kmeans.start();
		
	}

}