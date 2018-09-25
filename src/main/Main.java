package main;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import crh.ClientRequestHandler;
import srh.ServerRequestHandler;
import utils.ConnectionType;

public class Main {

	public static void main(String[] args) {
		new Thread() {
			@Override
			public void run() {
				
				try {
					ServerRequestHandler srh = new ServerRequestHandler(2424, ConnectionType.MIDDLEWARE);
					
					while (true) {
						System.out.println("esperando conexao");

						byte[] msg = srh.receive();
						System.out.println("mensagem recebida");

						for (byte b : msg) {
							System.out.print(b);
							System.out.print(" ");
						}
						System.out.println("");

						byte[] msgToClient = { 1, 4, 5 };
						System.out.println("mensagem para o client");

						for (byte b : msgToClient) {
							System.out.print(b);
							System.out.print(" ");
						}
						System.out.println("");

						srh.send(msgToClient);

					}
				} catch (IOException e) {
					e.printStackTrace();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (TimeoutException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.start();

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		

		try {
			ClientRequestHandler crh = new ClientRequestHandler("localhost", 2424, ConnectionType.MIDDLEWARE);
			
			byte[] msgToServer = { 1, 6, 9 };
			System.out.println("mensagem enviada para o servidor");

			for (byte b : msgToServer) {
				System.out.print(b);
				System.out.print(" ");
			}
			System.out.println("");

			crh.send(msgToServer);

			byte[] msgFromServer = crh.receive();
			System.out.println("mensagem recebida do servidor");

			for (byte b : msgFromServer) {
				System.out.print(b);
				System.out.print(" ");
			}
			System.out.println("");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
