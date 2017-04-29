package edu.rice.system;

import edu.rice.datamodel.TupleData;
import edu.rice.parser.RDFParser;

public class InputDataConsumer implements Runnable {

	
	RDFParser myParser;
	
	public InputDataConsumer(){
		myParser=new RDFParser();
		
	}
	
	
	
	@Override
	public void run() {
		   while (true) {
	            try {
	            // We send the payload for parsing and rest of the processing. 
	            	myParser.processData(consume().getPayload());
	            } catch (Exception ex) {
	            	ex.printStackTrace();
	            }

	        }
	}
 
	
	
	 private TupleData consume() throws InterruptedException {
	        //wait if queue is empty
	        while (InputCache.list.isEmpty()) {
	            synchronized (InputCache.list) {
	                InputCache.list.wait();
	            }
	        }

	        //Otherwise consume element and notify waiting producer
	        synchronized (InputCache.list) {
	        	InputCache.list.notifyAll();
	            return InputCache.list.remove(0);
	        }
	    }

}
