package crh;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;
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

import java.net.Socket;

import utils.ConnectionType;

public class ClientRequestHandler {

	private String host;
	private int port;
	private ConnectionType connectionType;

	Channel ch;

	//tcp
	private Socket socket;
	private DataInputStream inFromServer;
	private DataOutputStream outToServer;

	//udp
    private DatagramSocket datagramUDP;
    private InetAddress IPAddress;

    //middleware
    Channel channel;
    final BlockingQueue<byte[]> msg = new ArrayBlockingQueue<>(1);
    Consumer consumer;

	public ClientRequestHandler(String host, int port, ConnectionType connectionType) throws IOException, TimeoutException {
		this.host = host;
		this.port = port;
		this.connectionType = connectionType;

		switch (connectionType) {
			case TCP:
				this.socket = new Socket(host, port);
				this.inFromServer = new DataInputStream(socket.getInputStream());
				this.outToServer = new DataOutputStream(socket.getOutputStream());
				break;
			case UDP:
                datagramUDP = new DatagramSocket();
                IPAddress = InetAddress.getByName(host);
				break;
			case MIDDLEWARE:
                ConnectionFactory factory = new ConnectionFactory();
                factory.setHost(this.host);
                Connection connection = factory.newConnection();
                channel = connection.createChannel();

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

	public byte[] receive() throws Exception {
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

	private void sendUdp(byte[] msg) throws IOException, InterruptedException {


		byte[] bytes = new byte[4];
		ByteBuffer.wrap(bytes).putInt(msg.length);

		DatagramPacket sendSize = new DatagramPacket(bytes, bytes.length, IPAddress, port);

        datagramUDP.send(sendSize);


		DatagramPacket sendPacket = new DatagramPacket(msg, msg.length, IPAddress, port);

        datagramUDP.send(sendPacket);

	}

	private byte[] receiveUdp() throws IOException, InterruptedException {
		byte[] bytes = new byte[4];

		DatagramPacket receiveSize = new DatagramPacket(bytes, bytes.length);

        datagramUDP.receive(receiveSize);

		int size = ByteBuffer.wrap(bytes).getInt();

		byte[] receiveData = new byte[size];

		DatagramPacket receivePacket = new DatagramPacket(receiveData, size);

        datagramUDP.receive(receivePacket);

		return receiveData;
	}

	public void sendTcp(byte[] msg) throws IOException, InterruptedException {
		outToServer.writeInt(msg.length);
		outToServer.write(msg, 0 , msg.length);
	}

	public byte[] receiveTcp() throws IOException {
        int receivedMessageSize = inFromServer.readInt();
        byte[] msgReceived = new byte[receivedMessageSize];
        inFromServer.read(msgReceived, 0, receivedMessageSize);
        return msgReceived;
    }

	public void sendRMQ(byte[] msg) throws IOException, TimeoutException {
		channel.basicPublish("", "md1", null, msg);
	}

	public byte[] receiveRMQ() throws Exception {
		channel.basicConsume("md2", true, consumer);
		return msg.take();
	}

}
