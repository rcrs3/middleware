package srh;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import utils.ConnectionType;

public class ServerRequestHandler {
	private String hostClient;
	private int port;
	private int portClient;
	private ConnectionType connectionType;
	private DatagramSocket serverSocket;
	
	public ServerRequestHandler(int port, ConnectionType connectionType) throws UnknownHostException, SocketException {
		
		this.port = port;
		this.connectionType = connectionType;
		this.serverSocket = new DatagramSocket(3333);
		
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
		switch(connectionType) {
		case TCP:
			
		case UDP:
			receiveUdp();
		case MIDDLEWARE:
		
		}
	}
	
	private void receiveUdp() throws IOException, InterruptedException {
		byte[] receiveData = new byte[1024];
		
		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
		serverSocket.receive(receivePacket);
		String sentence = new String( receivePacket.getData());
        System.out.println("RECEIVED: " + sentence);
            
            
        this.portClient = receivePacket.getPort();
        this.hostClient = receivePacket.getAddress().getHostName();
	}
	
	private void sendUdp(byte[] msg) throws IOException, InterruptedException {
		
		InetAddress IPAddress = InetAddress.getByName(this.hostClient);
		
		DatagramPacket sendPacket = new DatagramPacket(msg, msg.length, IPAddress, this.portClient);
        
		serverSocket.send(sendPacket);
	}
}
