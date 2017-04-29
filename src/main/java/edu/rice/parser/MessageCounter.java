package edu.rice.parser;

public class MessageCounter {

	public static long counter = 0;

	public synchronized static void increment() {
		counter++;
	}

	public synchronized static void decrement() {
		counter--;
	}

	public static long getCounter() {
		return counter;
	}
}