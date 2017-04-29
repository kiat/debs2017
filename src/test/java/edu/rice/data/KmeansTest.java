package edu.rice.data;

import edu.rice.kmeans.CircularQueue;
import edu.rice.kmeans.KMeans;
import edu.rice.utils.Constants;

public class KmeansTest {

	public static void main(String[] args) {


		CircularQueue m_window = new CircularQueue(Constants.WINDOW_SIZE);
					  // 2.97  3.00  2.94  2.89  3.02  2.95  2.94  3.05  3.05  2.89
		double[] data = {2.97, 3.00, 2.94, 2.89 ,3.02, 2.95, 2.94, 3.05, 3.05, 2.89};
		
		for (int i = 0; i < data.length; i++) {
			m_window.insert(data[i]);	
		}
		
	
		for (int i = 0; i < 100000; i++) {
			KMeans singleKMeans = new KMeans();
			
			
			data[0] += 0.001;
			
			boolean hasAnomalies = singleKMeans.performAllCalculation(4, m_window, 0.005);
			
			if(hasAnomalies)
			System.out.println(singleKMeans.getThreshold());	
		}
		
		
	}

}