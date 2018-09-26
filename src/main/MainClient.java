package main;

import com.rabbitmq.client.*;
import crh.ClientRequestHandler;
import utils.ConnectionType;

import java.io.IOException;
import java.util.concurrent.TimeoutException;


public class MainClient {
    public static void main(String[] args) {
        ClientRequestHandler crh = null;
        try {
            crh = new ClientRequestHandler("localhost", 2323, ConnectionType.MIDDLEWARE);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        byte[] msg = new byte[1024];
        for(int j = 0; j < 10; j++) {
            long elapsedTime = 0;
            for (int i = 0; i < 10000; i++) {

                long currentTime = System.currentTimeMillis();
                try {
                    crh.send(msg);
                    crh.receive();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                elapsedTime += System.currentTimeMillis() - currentTime;

            }
            System.out.println(elapsedTime);
        }


//        ConnectionFactory factory = new ConnectionFactory();
//        factory.setHost("localhost");
//        Connection connection = null;
//        Channel channel = null;
//
//        try {
//            connection = factory.newConnection();
//            channel = connection.createChannel();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (TimeoutException e) {
//            e.printStackTrace();
//        }
//
//        final BlockingQueue<byte[]> msg = new ArrayBlockingQueue<>(1);
//        Consumer consumer = new DefaultConsumer(channel) {
//            @Override
//            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
//                                       byte[] body) throws IOException {
//                msg.add(body);
//            }
//        };
//
//        byte[] msgToServer = new byte[1024];
//        for(int j = 0; j < 10; j++) {
//            long elapsedTime = 0;
//            for (int i = 0; i < 2500; i++) {
//
//                long currentTime = System.currentTimeMillis();
//                try {
//                    channel.basicPublish("", "md1", null, msgToServer);
//                    channel.basicConsume("md2", true, consumer);
//                    msg.take();
//                } catch (IOException | InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//                elapsedTime += System.currentTimeMillis() - currentTime;
//
//            }
//            System.out.println(elapsedTime);
//        }
    }
}
