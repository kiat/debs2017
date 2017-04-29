package edu.rice.system;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Scanner;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class SerializeDataForSend {

	public static byte[] data;
	public static ArrayList<Integer> index;

	public static void main(String[] args) {

		index = new ArrayList<Integer>(5000);

		try {
			File file = new File("molding_machine_5000dp.nt");
			FileInputStream fis = new FileInputStream(file);
			data = new byte[(int) file.length()];
			fis.read(data);
			fis.close();

			System.out.println("Data is read. Building the index. ");

			String str = new String(data, "UTF-8");
			String[] lines = str.split(System.getProperty("line.separator"));

			String segment = "";
			String observationGroupTmp = "";

			for (int i = 0; i < lines.length; i++) {
				// split the string by space character.
				String[] tripleParts = lines[i].split(" ");

				if (tripleParts[0].contains("Group") && tripleParts[0].compareTo(observationGroupTmp) != 0) {
					if (segment.compareTo("") != 0) {
						byte[] messageBodyBytes = segment.getBytes();
						index.add(messageBodyBytes.length);
					}
					segment = "";
					observationGroupTmp = tripleParts[0];
				}
				segment = segment + lines[i] + "\n";
			}

			// one last send out
			byte[] messageBodyBytes = segment.getBytes();
			index.add(messageBodyBytes.length);

		} catch (Exception e) {
			e.printStackTrace();
		}

		
		
		
		
		// 
		System.out.println("Index is built. Write Data.");

		// Serialize to a byte array
		try {
			FileOutputStream indexFile = new FileOutputStream("molding_machine_5000dp.nt.index");
			BufferedOutputStream bos = new BufferedOutputStream(indexFile);
			ObjectOutput out = new ObjectOutputStream(bos);

			// here do the serialization
			out.writeObject(index);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	
		
//
//			// NOW send it
//			ConnectionFactory factory = new ConnectionFactory();
//			factory.setHost("localhost");
//			try {
//
//				// Connect to the RabbitMQ
//				Connection conn = factory.newConnection();
//				Channel channel = conn.createChannel();
//				channel.queueDeclarePassive("hobbit.datagen-system.exp1").getQueue();
//
//				// We get a random number and then read the text file line by
//				// line.
//				// We send each time a random number of lines as a text message
//				// to
//				// the rabbitMQ as a message.
//
//				int position = 0;
//
//				// Read File line by line and push it to rabbitMQ
//				for (int i = 0; i < index.size()-1; i++) {
//					byte[] messageBodyBytes = new byte[index.get(i)];
//					System.arraycopy(data, position, messageBodyBytes, 0, index.get(i));
//
//					// System.out.println(new String(messageBodyBytes,
//					// "UTF-8"));
//					channel.basicPublish("", "hobbit.datagen-system.exp1", null, messageBodyBytes);
//					position += index.get(i);
//				}
//
//				byte[] messageBodyBytes = new byte[index.get(index.size() - 1)];
//				System.arraycopy(data, position, messageBodyBytes, 0, index.get(index.size() - 1));
//				channel.basicPublish("", "hobbit.datagen-system.exp1", null, messageBodyBytes);
//
//				channel.close();
//				conn.close();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//
		
		}
}