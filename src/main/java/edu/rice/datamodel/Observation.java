package edu.rice.datamodel;

import java.net.URI;

public class Observation {

	// A URI that identifies this observation. 
	private URI observationURI;

	// An int name for this observation property. This the dimension number.  
	private int observeredPropertyName;
	
	// The actual observation result value as a double number. This might be an integer or a float number. 
	// We can maybe consider a float for this. 
	private double observationResult;

	public URI getObservationURI() {
		return observationURI;
	}

	public void setObservationURI(URI observationURI) {
		this.observationURI = observationURI;
	}

	public int getObserveredPropertyName() {
		return observeredPropertyName;
	}

	public void setObserveredPropertyName(int observeredPropertyName) {
		this.observeredPropertyName = observeredPropertyName;
	}

	public double getObservationResult() {
		return observationResult;
	}

	public void setObservationResult(double observationResult) {
		this.observationResult = observationResult;
	}
	
	
	public Observation() {

	}

	public Observation(URI observationURI, int observeredPropertyName, double observationResult) {
		this.observationURI = observationURI;
		this.observeredPropertyName = observeredPropertyName;
		this.observationResult = observationResult;
	}

}
