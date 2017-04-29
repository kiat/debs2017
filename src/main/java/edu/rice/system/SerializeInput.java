package edu.rice.system;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.*;
import java.util.LinkedList;
import java.util.concurrent.TimeoutException;

public class SerializeInput {
    public static void main(String[] args) throws ClassNotFoundException, InterruptedException {

        ConnectionFactory factory = new ConnectionFactory();

        factory.setHost("localhost");

        LinkedList<byte[]> data = new LinkedList<>();

        try {

            // Connect to the RabbitMQ
            Connection conn = factory.newConnection();
            Channel channel = conn.createChannel();
            channel.queueDeclarePassive("hobbit.datagen-system.exp1").getQueue();

            FileInputStream fileIn = new FileInputStream("../resources/molding_machine_5000dp.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            data = (LinkedList<byte[]>) in.readObject();
            in.close();
            fileIn.close();

            while(true) {
                Thread.sleep(1);
                for (byte[] m : data) {
                    channel.basicPublish("", "hobbit.datagen-system.exp1", null, m);
                }
            }


            //channel.close();
            //conn.close();

        } catch (IOException | TimeoutException e) {
            e.printStackTrace();
        }

    }
}
