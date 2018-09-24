package crh;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import utils.ConnectionType;

public class ClientRequestHandler {

	private String host;
	private int port;
	private ConnectionType connectionType;
	private byte[] msgReceived;
	private int receivedMessageSize;

	public ClientRequestHandler(String host, int port, ConnectionType connectionType) {
		this.host = host;
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
		}
	}

	public byte[] receive() {
		switch (connectionType) {
		case TCP:
			return this.msgReceived;
		case UDP:

			break;
		case MIDDLEWARE:

			break;
		}
		return null;
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
