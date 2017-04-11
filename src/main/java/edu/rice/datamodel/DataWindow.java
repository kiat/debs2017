package edu.rice.datamodel;

import java.util.List;

public class DataWindow {

	// A list of observation groups will be in a data window
	private List<ObservationGroup> observationGroups;

	// default constructor.
	public DataWindow() {

	}

	public DataWindow(List<ObservationGroup> observationGroups) {
		super();
		this.observationGroups = observationGroups;
	}

	public List<ObservationGroup> getObservationGroups() {
		return observationGroups;
	}

	public void setObservationGroups(List<ObservationGroup> observationGroups) {
		this.observationGroups = observationGroups;
	}

	/**
	 * Adds an observationGroup Object to the list.
	 * 
	 * @param obsGr
	 *            an ObserveraionGroup object
	 */
	public void addObservationGr(ObservationGroup obsGr) {
		this.observationGroups.add(obsGr);
	}

	/**
	 * Removes an obserationGroup Object from the list.
	 * 
	 * @param obsGr
	 */
	public void removeObservationGr(ObservationGroup obsGr) {
		this.observationGroups.remove(obsGr);
	}

}