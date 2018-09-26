package srh;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeoutException;


import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import utils.ConnectionType;

public class ServerRequestHandler {
	private int port;
	private ConnectionType connectionType;

	//tcp
	private DataInputStream inFromClient;
	private DataOutputStream outToClient;

	//udp
	private DatagramSocket datagramUDP;
	private int clientPort;
	private InetAddress clientAdress;

	//middleware
	Channel channel;
	final BlockingQueue<byte[]> msg = new ArrayBlockingQueue<>(1);
	Consumer consumer;
	public ServerRequestHandler(int port, ConnectionType connectionType) throws IOException, TimeoutException {
		super();
		this.port = port;
		this.connectionType = connectionType;



		switch (connectionType) {
			case TCP:
				ServerSocket serverSocketTcp = new ServerSocket(port);
				Socket socket = serverSocketTcp.accept();
				inFromClient = new DataInputStream(socket.getInputStream());
				outToClient = new DataOutputStream(socket.getOutputStream());
				break;
			case UDP:
				datagramUDP = new DatagramSocket(this.port);
				break;
			case MIDDLEWARE:
				ConnectionFactory factory = new ConnectionFactory();
				factory.setHost("localhost");
				Connection connection = factory.newConnection();
				channel = connection.createChannel();

				Map<String, Object> args = new HashMap<String, Object>();
				args.put("x-max-length", 1);

				channel.queueDeclare("md1", false, false, false, args);
				channel.queueDeclare("md2", false, false, false, args);

				consumer = new DefaultConsumer(channel) {
					@Override
					public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
											   byte[] body) throws IOException {
						msg.add(body);
					}
				};
				break;
		}
	}

	public void send(byte[] msg) throws IOException, InterruptedException, TimeoutException {
		switch (connectionType) {
		case TCP:
			sendTcp(msg);
			break;
		case UDP:
			sendUdp(msg);
			break;
		case MIDDLEWARE:
			sendRMQ(msg);
			break;
		}
	}

	public byte[] receive() throws IOException, InterruptedException, TimeoutException {
		switch (connectionType) {
		case TCP:
			return receiveTcp();
		case UDP:
			return receiveUdp();
		case MIDDLEWARE:
			return receiveRMQ();
		}
		return null;
	}

	private byte[] receiveTcp() throws IOException {
		int receivedMessageSize = inFromClient.readInt();
		byte[] msg = new byte[receivedMessageSize];
		inFromClient.read(msg, 0, receivedMessageSize);
		return msg;
	}

	public void sendTcp(byte[] msg) throws IOException, InterruptedException {
		outToClient.writeInt(msg.length);
		outToClient.write(msg, 0, msg.length);
	}

	private byte[] receiveUdp() throws IOException, InterruptedException {
		byte[] bytes = new byte[4];

		DatagramPacket receiveSize = new DatagramPacket(bytes, bytes.length);
		datagramUDP.receive(receiveSize);

		int size = ByteBuffer.wrap(bytes).getInt();

		byte[] receiveData = new byte[size];

		DatagramPacket receivePacket = new DatagramPacket(receiveData, size);

		datagramUDP.receive(receivePacket);

		this.clientPort = receivePacket.getPort();
		this.clientAdress = receivePacket.getAddress();

		return receiveData;
	}

	private void sendUdp(byte[] msg) throws IOException, InterruptedException {
		byte[] bytes = new byte[4];
		ByteBuffer.wrap(bytes).putInt(msg.length);

		DatagramPacket sendSize = new DatagramPacket(bytes, bytes.length, clientAdress, clientPort);

		datagramUDP.send(sendSize);

		DatagramPacket sendPacket = new DatagramPacket(msg, msg.length, clientAdress, clientPort);

		datagramUDP.send(sendPacket);
	}

	public void sendRMQ(byte[] msg) throws IOException, TimeoutException {
		channel.basicPublish("", "md2", null, msg);
	}

	public byte[] receiveRMQ() throws IOException, TimeoutException, InterruptedException {
		channel.basicConsume("md1", true, consumer);
		return msg.take();
	}

}
