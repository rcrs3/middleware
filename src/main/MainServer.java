package main;

import com.rabbitmq.client.*;
import srh.ServerRequestHandler;
import utils.ConnectionType;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeoutException;

public class MainServer {
    public static void main(String[] args) {
        ServerRequestHandler srh = null;
        try {
            srh = new ServerRequestHandler(2323, ConnectionType.MIDDLEWARE);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        byte[] msg = new byte[1024];
        while (true){
            try {
                srh.receive();
                srh.send(msg);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (TimeoutException e) {
                e.printStackTrace();
            }

        }
//        ConnectionFactory factory = new ConnectionFactory();
//        factory.setHost("localhost");
//        Connection connection = null;
//        try {
//            connection = factory.newConnection();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (TimeoutException e) {
//            e.printStackTrace();
//        }
//        Channel channel = null;
//        try {
//            channel = connection.createChannel();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//
//        Map<String, Object> args1 = new HashMap<String, Object>();
//        args1.put("x-max-length", 1);
//
//        try {
//            channel.queueDeclare("md1", false, false, false, args1);
//            channel.queueDeclare("md2", false, false, false, args1);
//        } catch (IOException e) {
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
//        byte[] msgToClient = new byte[1024];
//
//        while(true){
//            try {
//                channel.basicConsume("md1", true, consumer);
//                msg.take();
//                channel.basicPublish("", "md2", null, msgToClient);
//            } catch (IOException | InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
    }
}
