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
import java.util.Arrays;

import javax.management.RuntimeErrorException;

import org.omg.PortableServer.ThreadPolicyOperations;

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

	public ServerRequestHandler(int port, ConnectionType connectionType) {
		this.port = port;
		this.connectionType = connectionType;
	}

	public void send(byte[] msg) throws IOException, InterruptedException {
		switch (connectionType) {
		case TCP:
			sendTcp(msg);
			break;
		case UDP:
			sendUdp(msg);
			break;
		case MIDDLEWARE:

			break;

		default:
			break;
		}
	}

	public byte[] receive() throws IOException, InterruptedException {
		switch (connectionType) {
		case TCP:
			return receiveTcp();
		case UDP:
			return receiveUdp();
		case MIDDLEWARE:

			break;

		default:
			break;
		}
		return null;
	}
	
	private byte[] receiveTcp() throws IOException {
		if(serverSocketTcp == null) {
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
		if(socket == null) {
			throw new RuntimeException("none accepted connection");
		}
		
		DataOutputStream outToClient = new DataOutputStream(socket.getOutputStream());
		outToClient.writeInt(msg.length);
		outToClient.write(msg);
		socket.close();
	}
	
	private byte[] receiveUdp() throws IOException, InterruptedException {
		if(serverSocketUdp == null)
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
}
