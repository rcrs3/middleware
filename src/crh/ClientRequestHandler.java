package crh;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.net.Socket;


import utils.ConnectionType;

public class ClientRequestHandler {

	private String host;
	private int port;
	private ConnectionType connectionType;
	private DatagramSocket clientSocket;
	private byte[] msgReceived;
	private int receivedMessageSize;

	public ClientRequestHandler(String host, int port, ConnectionType connectionType) {
		this.host = host;
		this.port = port;
		this.connectionType = connectionType;
	}

	public void send(byte[] msg) throws IOException, InterruptedException {
		switch(connectionType) {
		case TCP:
			sendTcp(msg);
		case UDP:
			sendUdp(msg);
		case MIDDLEWARE:
		
		}
	}
	
	public byte[] receive() throws IOException, InterruptedException {
		switch (connectionType) {
		case TCP:
			return this.msgReceived;
		case UDP:
			return receiveUdp();
		case MIDDLEWARE:

			break;
		}
		return null;
	}
	
	private void sendUdp(byte[] msg) throws IOException, InterruptedException {
		if(clientSocket == null)
			this.clientSocket = new DatagramSocket();
		
		InetAddress IPAddress = InetAddress.getByName(host);
		
		byte[] bytes = new byte[4];
		ByteBuffer.wrap(bytes).putInt(msg.length);

		DatagramPacket sendSize = new DatagramPacket(bytes, bytes.length, IPAddress, port);
	    
		clientSocket.send(sendSize);
		
		Thread.sleep(10);
	
		DatagramPacket sendPacket = new DatagramPacket(msg, msg.length, IPAddress, port);
	    
		clientSocket.send(sendPacket);
		
	}
	
	private byte[] receiveUdp() throws IOException, InterruptedException {
		if(clientSocket == null)
			this.clientSocket = new DatagramSocket();
		
		byte[] bytes = new byte[4];
		
		
		DatagramPacket receiveSize = new DatagramPacket(bytes, bytes.length);
		
		clientSocket.receive(receiveSize);
		
		int size = ByteBuffer.wrap(bytes).getInt();

		byte[] receiveData = new byte[size];
		
		DatagramPacket receivePacket = new DatagramPacket(receiveData, size);
		 
		clientSocket.receive(receivePacket);

		return receiveData;   
	}

	public void sendTcp(byte[] msg) throws IOException, InterruptedException {
		Socket socket = new Socket(this.host, this.port);
		DataOutputStream outToServer = new DataOutputStream(socket.getOutputStream());
		DataInputStream inFromServer = new DataInputStream(socket.getInputStream());
		outToServer.writeInt(msg.length);
		outToServer.write(msg);
		this.receivedMessageSize = inFromServer.readInt();
		msgReceived = new byte[this.receivedMessageSize];
		inFromServer.read(this.msgReceived);
		socket.close();
	}
}
