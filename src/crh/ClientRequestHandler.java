package crh;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import utils.ConnectionType;

public class ClientRequestHandler {
	
	private String host;
	private int port;
	private ConnectionType connectionType;
	private DatagramSocket clientSocket;
	
	public ClientRequestHandler(String host, int port, ConnectionType connectionType) throws UnknownHostException, SocketException {
		this.host = host;
		this.port = port;
		this.connectionType = connectionType;
		
		this.clientSocket = new DatagramSocket();
	}
	
	public void send(byte[] msg) throws IOException, InterruptedException {
		switch(connectionType) {
		case TCP:
			
		case UDP:
			sendUdp(msg);
		case MIDDLEWARE:
		
		}
	}
	
	public void receive() throws IOException, InterruptedException {
		if(connectionType == ConnectionType.UDP) {
			receiveUdp();
		}
	}
	
	private void sendUdp(byte[] msg) throws IOException, InterruptedException {
		InetAddress IPAddress = InetAddress.getByName(host);
		DatagramPacket sendPacket = new DatagramPacket(msg, msg.length, IPAddress, port);
	    
		clientSocket.send(sendPacket);
		
	}
	
	private void receiveUdp() throws IOException, InterruptedException {
		byte[] receiveData = new byte[1024];
		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
		 
		clientSocket.receive(receivePacket);

		String modifiedSentence = new String(receivePacket.getData());
	    System.out.println("FROM SERVER:" + modifiedSentence);
	    
	}
}
