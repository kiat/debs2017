package edu.rice.utils;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import edu.rice.datamodel.TupleData;
import edu.rice.parser.RDFParserToSensordata;

public class StreamBuilders {

	static LinkedList<byte[]> data = new LinkedList<>();

	public static void main(String[] args) {

		FileInputStream fileIn;

		try {
			fileIn = new FileInputStream("molding_machine_5000dp.ser");

			ObjectInputStream in = new ObjectInputStream(fileIn);
			data = (LinkedList<byte[]>) in.readObject();
			in.close();
			fileIn.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		List<TupleData> list = new ArrayList<TupleData>();

		for (long i = 0; i < data.size(); i++) {
			try {
				new RDFParserToSensordata(i).processData(data.remove());
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		//
		//
		// for(long i = 1; i< list.size(); i++){
		// list.add(new TupleData( i , data.remove() ));
		// }
		//
		//
		// //Here creating a parallel stream
		// Stream<TupleData> stream = list.parallelStream();
		//
		//
		// Integer[] evenNumbersArr = stream.filter(i -> i%2 == 0).toArray(Integer[]::new);
		// System.out.print(evenNumbersArr.length);
	}
}