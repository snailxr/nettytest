package snailxr.bio;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
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
				BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
				PrintWriter out = new PrintWriter(new BufferedWriter(
						new OutputStreamWriter(socket.getOutputStream())), true);
				while (true) {
					String text = in.readLine();
					System.out.println("text from client: " + text);
					out.println(text);
					if ("exit".equals(text)) {
						out.println("exit");
						socket.close();
						break;
					}
				}
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
