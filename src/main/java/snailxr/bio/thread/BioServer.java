package snailxr.bio.thread;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class BioServer {
	private ServerSocket server;

	public BioServer(int port) throws IOException {
		server = new ServerSocket(port);
	}

	public void listen() throws IOException {
		System.out.println("server started.........................");
		Socket socket = null;
		try {
			while (true) {
				socket = server.accept();
				new Thread(new BioServerThread(socket)).start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			server.close();
		}
	}
	
	public static void main(String[] args) throws IOException {
		BioServer server = new BioServer(9000);
		server.listen();
	}
}
