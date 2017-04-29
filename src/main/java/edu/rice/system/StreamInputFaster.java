package edu.rice.system;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.util.ArrayList;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class StreamInputFaster {
	
	public static byte[] data;
	public static ArrayList<Integer> index;
	
	public static void main(String[] args) {
		
		System.out.println("Reading Data File");

		index = new ArrayList<Integer>(5000);

		try {
			File file = new File("molding_machine_5000dp.nt");
			FileInputStream fis = new FileInputStream(file);
			data = new byte[(int) file.length()];
			fis.read(data);
			fis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("Data file is read. Reading index File ");

		
		
		
		// Serialize to a byte array
		try {
			FileInputStream indexFile = new FileInputStream("molding_machine_5000dp.nt.index");
			BufferedInputStream bos = new BufferedInputStream(indexFile);
			ObjectInput input = new ObjectInputStream(bos);
			
			// here do the serialization
			index = (ArrayList<Integer>)input.readObject();
			input.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Publising data to RabbitMQ ");

		
		
		//
//		// NOW send it
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		try {

			// Connect to the RabbitMQ
			Connection conn = factory.newConnection();
			Channel channel = conn.createChannel();
			channel.queueDeclarePassive("hobbit.datagen-system.exp1").getQueue();

			// We get a random number and then read the text file line by
			// line.
			// We send each time a random number of lines as a text message
			// to
			// the rabbitMQ as a message.

			int position = 0;

			// Read File line by line and push it to rabbitMQ
			for (int i = 0; i < index.size()-1; i++) {
				byte[] messageBodyBytes = new byte[index.get(i)];
				System.arraycopy(data, position, messageBodyBytes, 0, index.get(i));

				// System.out.println(new String(messageBodyBytes,
				// "UTF-8"));
				channel.basicPublish("", "hobbit.datagen-system.exp1", null, messageBodyBytes);
				position += index.get(i);
			}

//			byte[] messageBodyBytes = new byte[index.get(index.size() - 1)];
//			System.arraycopy(data, position, messageBodyBytes, 0, index.get(index.size() - 1));
//			channel.basicPublish("", "hobbit.datagen-system.exp1", null, messageBodyBytes);

			channel.close();
			conn.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
