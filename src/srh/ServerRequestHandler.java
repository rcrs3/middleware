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
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeoutException;


import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Delivery;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.RpcServer;
import com.rabbitmq.client.StringRpcServer;

import utils.ConnectionType;

public class ServerRequestHandler {
	private int port;
	private ConnectionType connectionType;
	private Socket socket;
	private ServerSocket serverSocketTcp;
	private int receivedMessageSize;
	private DatagramSocket serverSocketUdp;
	private String hostClient;
	private int portClient;
	private String msgToSent;

	public ServerRequestHandler(int port, ConnectionType connectionType) throws RemoteException {
		super();
		this.port = port;
		this.connectionType = connectionType;
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
		if (serverSocketTcp == null) {
			serverSocketTcp = new ServerSocket(port);
		}
		this.socket = serverSocketTcp.accept();
		DataInputStream inFromClient = new DataInputStream(socket.getInputStream());
		this.receivedMessageSize = inFromClient.readInt();

		byte[] msg = new byte[this.receivedMessageSize];
		inFromClient.read(msg);
		return msg;
	}

	public void sendTcp(byte[] msg) throws IOException, InterruptedException {
		if (socket == null) {
			throw new RuntimeException("none accepted connection");
		}

		DataOutputStream outToClient = new DataOutputStream(socket.getOutputStream());
		outToClient.writeInt(msg.length);
		outToClient.write(msg);
		socket.close();
	}

	private byte[] receiveUdp() throws IOException, InterruptedException {
		if (serverSocketUdp == null)
			serverSocketUdp = new DatagramSocket(this.port);

		byte[] bytes = new byte[4];

		DatagramPacket receiveSize = new DatagramPacket(bytes, bytes.length);
		serverSocketUdp.receive(receiveSize);

		int size = ByteBuffer.wrap(bytes).getInt();

		byte[] receiveData = new byte[size];

		DatagramPacket receivePacket = new DatagramPacket(receiveData, size);

		serverSocketUdp.receive(receivePacket);

		this.portClient = receivePacket.getPort();
		this.hostClient = receivePacket.getAddress().getHostName();

		return receiveData;
	}

	private void sendUdp(byte[] msg) throws IOException, InterruptedException {
		DatagramSocket s = new DatagramSocket();
		InetAddress IPAddress = InetAddress.getByName(this.hostClient);

		byte[] bytes = new byte[4];
		ByteBuffer.wrap(bytes).putInt(msg.length);

		DatagramPacket sendSize = new DatagramPacket(bytes, bytes.length, IPAddress, this.portClient);

		s.send(sendSize);

		Thread.sleep(10);

		DatagramPacket sendPacket = new DatagramPacket(msg, msg.length, IPAddress, this.portClient);

		s.send(sendPacket);
	}

	public void sendRMQ(byte[] msg) throws IOException, TimeoutException {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();

		channel.queueDeclare("md1", false, false, false, null);
		channel.basicPublish("", "md2", null, msg);
	}

	public byte[] receiveRMQ() throws IOException, TimeoutException, InterruptedException {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();

		channel.queueDeclare("md1", false, false, false, null);

		final BlockingQueue<byte[]> msg = new ArrayBlockingQueue<>(1);
		Consumer consumer = new DefaultConsumer(channel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
					byte[] body) throws IOException {
				msg.add(body);
			}
		};
		channel.basicConsume("md1", true, consumer);
		return msg.take();
	}

}
