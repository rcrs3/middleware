package srh;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.management.RuntimeErrorException;

import org.omg.PortableServer.ThreadPolicyOperations;

import utils.ConnectionType;

public class ServerRequestHandler {
	private int port;
	private ConnectionType connectionType;
	private Socket socket;
	private ServerSocket serverSocket;
	private int receivedMessageSize;

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

			break;
		case MIDDLEWARE:

			break;

		default:
			break;
		}
		return null;
	}
	
	private byte[] receiveTcp() throws IOException {
		if(serverSocket == null) {
			serverSocket = new ServerSocket(port);
		}
		this.socket = serverSocket.accept();
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
}
