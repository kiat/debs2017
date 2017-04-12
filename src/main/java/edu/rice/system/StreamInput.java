package edu.rice.system;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class StreamInput {
    public static void main(String[] args) {

        ConnectionFactory factory = new ConnectionFactory();

        factory.setHost("localhost");

        try {

            // Connect to the RabbitMQ
            Connection conn = factory.newConnection();
            Channel channel = conn.createChannel();
            channel.queueDeclarePassive("hobbit.datagen-system.exp1").getQueue();

            // Open the file and buffer it
            File file = new File(ClassLoader.getSystemClassLoader().getResource("molding_machine_10M.nt").getFile());
            BufferedReader br = new BufferedReader(new FileReader(file));

            String line;
            String segment = "";
            String observationGroupTmp = "";


            // We get a random number and then read the text file line by line.
            // We send each time a random number of lines as a text message to
            // the rabbitMQ as a message.

            // Read File line by line and push it to rabbitMQ
            while ((line = br.readLine()) != null) {

                // split the string by space character.
                String[] tripleParts = line.split(" ");

                if (tripleParts[0].contains("Group") && tripleParts[0].compareTo(observationGroupTmp) !=0 ) {

                    if (segment.compareTo("")!=0) {
                        byte[] messageBodyBytes = segment.getBytes();
                        channel.basicPublish("", "hobbit.datagen-system.exp1", null, messageBodyBytes);
                    }

                    segment = "";
                    observationGroupTmp = tripleParts[0];
                }

                segment = segment + line + "\n";
            }

            br.close();
            channel.close();
            conn.close();

        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }

    }
}
