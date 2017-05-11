package edu.rice.kmeans;

import java.util.HashSet;

public class CircularQueue {

	// Number of elements:
	private int windowSize = -1;

	// Size of the queue:
	private int countElements = 0;

	// TODO This needs to go copying objects is expensive, having an array of
	// objects is like having an array of pointers so you need to accesses to
	// get the value, if you have an array of primitive types that's a nice
	// chunk of memory...
	private Point[] points;

	// TODO don't need this too expensive to keep the first and the next just go
	// and overwrite the values.
	private int first, next;

	// Default constructor:
	// TODO there should be a copy constructor that copies the the data with
	// Arrays.copyOf less expensive
	public CircularQueue(int _windowSize) {
		this.windowSize = _windowSize;
		this.countElements = 0;
		this.points = new Point[this.windowSize];
		this.first = 0;
		this.next = 0;
	}

	// Check if queue is full:
	private boolean full() {
		return (countElements == windowSize);
	}

	// Check if queue is empty:
	// TODO why do you need this?
	public boolean empty() {
		return (countElements == 0);
	}

	// Size of the queue:
	// TODO Also might not be required if you have full
	public int size() {
		return countElements;
	}

	// Insert a point:
	// TODO this is to complicated the insert should just calculate the new next
	// and write the value
	public void insert(Double value) {
		if (!full()) {
			countElements++;
			points[next] = new Point(value);
			next = (next + 1) % windowSize;
		} else {
			System.out.println("ERROR : Overflow Exception!");
		}
	}

	// Delete a point:
	// TODO this is not required should be removed, implementing it might cause
	// performance issues
	public Double remove() {
		if (!empty()) {
			countElements--;
			Point result = points[first];
			first = (first + 1) % windowSize;
			return result.getX();
		} else {
			System.out.println("ERROR : Underflow Exception!");
			return null;
		}
	}

	// Display the window of points:
	public void display() {
		System.out.print("[ ");
		for (int i = 0; i < countElements; i++) {
			System.out.print(String.format("%.2f", points[(first + i) % windowSize].getX()) + " ");
		}
		System.out.print("]\n");
	}

	public String toString() {
		String tmp = ("[ ");
		for (int i = 0; i < countElements; i++) {
			tmp += (String.format("%.2f", points[(first + i) % windowSize].getX()) + " ");
		}
		tmp += ("]");
		return tmp;
	}

	// Get point at any index:
	// TODO after you make the changes this needs to be updated
	public Point get(int index) {
		if (index < 0) {
			return null;
		}
		if (index < countElements) {
			return points[(first + index) % windowSize];
		}
		return null;
	}

	public boolean isAllUnique() {
		double tmp = points[0].getX();

		for (Point point : points) {
			if (tmp != point.getX())
				return false;
			else
				tmp = point.getX();
		}

		return true;
	}
	
	
	
	
	
	// Returns number of unique points:
	public int getNumberOfUniquePoints() {
		int countUnique = 0;
		HashSet <Double> uniquePoints = new HashSet <Double>();
		
		// Current value:
		double curValue;
		for (Point point : points) {			
				curValue = point.getX();
				if(!uniquePoints.contains(curValue)) {
					uniquePoints.add(curValue);			
									
					//Increment unique points count:
					countUnique++;	
				}				
		}		
		return countUnique;
	}

	// public static void main(String[] args) {
	// CircularQueue cq = new CircularQueue(10);
	//
	// for (int i = 0; i < 10; i++) {
	// cq.insert(i * 0.1);
	// System.out.println("Inserting: " + String.format("%.2f",(i * 0.1)));
	// }
	// cq.display();
	//
	// for (int i = 0; i < 10; i++) {
	// cq.remove();
	// cq.insert(i * 0.2);
	// System.out.println("Inserting: " + String.format("%.2f",(i * 0.2)));
	// cq.display();
	//
	// System.out.println("0th index is: " +
	// String.format("%.2f",cq.get(0).getX()));
	// System.out.println("4th index is: " +
	// String.format("%.2f",cq.get(4).getX()));
	// }
	// }
}