package main;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.plaf.SliderUI;

import crh.ClientRequestHandler;
import srh.ServerRequestHandler;
import utils.ConnectionType;

public class Main {

	public static void main(String[] args) {
		new Thread() {
			@Override
			public void run() {
				ServerRequestHandler srh = new ServerRequestHandler(2424, ConnectionType.TCP);
				try {
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
				}
			}
		}.start();

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		ClientRequestHandler crh = new ClientRequestHandler("localhost", 2424, ConnectionType.TCP);

		try {
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
		}

	}

}
