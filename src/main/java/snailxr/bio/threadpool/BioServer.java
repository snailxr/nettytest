package snailxr.bio.threadpool;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import snailxr.bio.thread.BioServerThread;

public class BioServer {
	private ServerSocket server;

	public BioServer(int port) throws IOException {
		server = new ServerSocket(port);
	}

	public void listen() throws IOException {
		BlockingQueue<Runnable> block = new ArrayBlockingQueue<Runnable>(1);
		/**
		 * 
		 * 1）当池子大小小于corePoolSize就新建线程，并处理请求
		 * 2）当池子大小等于corePoolSize，把请求放入workQueue中，池子里的空闲线程就去从workQueue中取任务并处理
		 * 3）当workQueue放不下新入的任务时，新建线程入池，并处理请求，
		 * 如果池子大小撑到了maximumPoolSize就用RejectedExecutionHandler来做拒绝处理
		 */
		ThreadPoolExecutor pool = new ThreadPoolExecutor(1, // corePoolSize
															// 池中保存的线程数，包括空闲线程
				2,// maximumPoolSize池中允许的最大线程数
				30, // 当线程数大于核心时，此为终止前 多余的空闲线程等待新任务的最长时间，单位由下个参数设置。
				TimeUnit.MINUTES, // 时间的单位
				block, new RejectedExecutionHandler() {

					@Override
					public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
						System.out.println(r.getClass().getName());
						System.out.println("服务数量达到顶峰啦..........................................");

						try {

							BioServerThread bioServer = (BioServerThread) r;
							Socket socket = bioServer.getSocket();
							PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
							out.println("busy");
							//socket.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});// 任务队列
		System.out.println("server started.........................");
		Socket socket = null;
		try {
			while (true) {
				socket = server.accept();
				BioServerThread thread = new BioServerThread(socket);
				pool.execute(thread);
				System.out.println(pool.getActiveCount());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			server.close();
			pool.shutdown();
		}
	}

	public static void main(String[] args) throws IOException {
		BioServer server = new BioServer(9000);
		server.listen();
	}
}
