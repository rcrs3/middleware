package main;

import crh.ClientRequestHandler;
import srh.ServerRequestHandler;
import utils.ConnectionType;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ClientRequestHandler client;
		try {
			client = new ClientRequestHandler("localhost", 3333, ConnectionType.UDP);
			
			String str = "SENDEI";
			byte[] b = str.getBytes();
			
			
			
			new Thread() {
				public void run() {
					
					try {
						
							client.send(b);
							client.receive();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}.start();
			
			
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		ServerRequestHandler server;
		try {
			server = new ServerRequestHandler(3333, ConnectionType.UDP);

			new Thread() {
				public void run() {
					try {
						while(true) {
							server.receive();
							String str = "AAAAA";
							byte[] b = str.getBytes();
							
							server.send(b);
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}.start();
			
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}

}
