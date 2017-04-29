package edu.rice.system;

import java.util.LinkedList;

import edu.rice.datamodel.TupleData;

public class InputCache {

	// Create a list shared by producer and consumer
	// Size of list is defined by capacity.
	public static int capacity = 1000;
	public static LinkedList<TupleData> list = new LinkedList<TupleData>();
}