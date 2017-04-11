package edu.rice.datamodel;

import java.net.URI;
import java.sql.Timestamp;
import java.util.List;

public class ObservationGroup {

	// The URI of this observation Group. This URI identifies this observation.
	private URI observationGroupURI;

	// The timestamp of this observation group
	private Timestamp timestamp;

	// A list of observations that are contained in this group.
	private List<Observation> observations;

	
	// default constructor
	public ObservationGroup() {
	}

	public ObservationGroup(URI observationGroupURI, Timestamp timestamp, List<Observation> observations) {
		super();
		this.observationGroupURI = observationGroupURI;
		this.timestamp = timestamp;
		this.observations = observations;
	}

	public URI getObservationGroupURI() {
		return observationGroupURI;
	}

	public List<Observation> getObservations() {
		return observations;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setObservationGroupURI(URI observationGroupURI) {
		this.observationGroupURI = observationGroupURI;
	}

	public void setObservations(List<Observation> observations) {
		this.observations = observations;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * Adds an Observation Object to the list.
	 * 
	 * @param obs
	 *            an Observation object
	 */
	public void addObservationGr(Observation obs) {
		this.observations.add(obs);
	}

	/**
	 * Removes an Observation Object from the list.
	 * 
	 * @param obs
	 */
	public void removeObservationGr(Observation obs) {
		this.observations.remove(obs);
	}

}