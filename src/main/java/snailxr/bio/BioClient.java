package snailxr.bio;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class BioClient {
	private Socket socket;

	public BioClient(String host, int port) throws UnknownHostException, IOException {
		socket = new Socket(host, port);

	}

	public void send() throws IOException {
		try {
			PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
			out.println("呵呵呵");
			out.println("hello server");
			out.println("哈哈哈");
			out.println("exit");
			BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			while (true) {
				String text = in.readLine();
				System.out.println(text);
				if ("exit".equals(text)||"busy".equals(text)) {
					break;
				}
			}
		} finally {
			socket.close();
		}
	}

	public static void main(String[] args) throws UnknownHostException, IOException {
		for (int i = 0; i < 10; i++) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					BioClient client;
					try {
						client = new BioClient("127.0.0.1", 9000);
						client.send();
					} catch (UnknownHostException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}).start();
		}

	}
}