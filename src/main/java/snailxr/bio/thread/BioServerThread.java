package snailxr.bio.thread;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class BioServerThread extends Thread {
	private Socket socket;

	public Socket getSocket() {
		return socket;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

	public BioServerThread(Socket socket) throws IOException {
		this.socket = socket;
	}

	@Override
	public void run() {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter out = new PrintWriter(new BufferedWriter(
					new OutputStreamWriter(socket.getOutputStream())), true);
			while (true) {
				String text = in.readLine();
				System.out.println(Thread.currentThread().getName() + " text from client: " + text);
				out.println(text);
				if ("exit".equals(text)) {
					out.println("exit");
					socket.close();
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
