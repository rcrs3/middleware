package crh;

import java.io.IOException;

import utils.ConnectionType;

public class ClientRequestHandler {
	
	private String host;
	private int port;
	private ConnectionType connectionType;
	
	public ClientRequestHandler(String host, int port, ConnectionType connectionType) {
		this.host = host;
		this.port = port;
		this.connectionType = connectionType;
	}
	
	public void send(byte[] msg) throws IOException, InterruptedException {
		if(connectionType == ConnectionType.UDP) {
			System.out.println("ENTROU UDP");
			sendUdp(msg);
		}
	}
	
	public void receive() throws IOException, InterruptedException {
		
	}
	
	public void sendUdp(byte[] msg) throws IOException, InterruptedException {
		byte[] sendData = new byte[1024];
	}
	
}
