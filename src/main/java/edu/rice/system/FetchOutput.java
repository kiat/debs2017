package edu.rice.system;

import com.rabbitmq.client.*;
import java.io.IOException;

public class FetchOutput {

    public static void main(String[] args) {

        ConnectionFactory factory = new ConnectionFactory();

        factory.setHost("localhost");

        try {

            // Connect to the RabbitMQ
            Connection conn = factory.newConnection();
            Channel channel = conn.createChannel();
            channel.queueDeclarePassive("hobbit.system-evalstore.exp1").getQueue();

            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope,
                                           AMQP.BasicProperties properties, byte[] body)
                        throws IOException {
                    String message = new String(body, "UTF-8");
                    System.out.println(message);
                }
            };

            Thread.sleep(60 * 10000);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
