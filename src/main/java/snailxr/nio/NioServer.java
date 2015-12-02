package snailxr.nio;


import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class NioServer {
  private int port;
  private ServerSocketChannel serverSocketChannel;
  private Selector selector;
  private int BLOCK_BUFFER=32;
  private ByteBuffer sendBuffer=ByteBuffer.allocate(BLOCK_BUFFER);
  private ByteBuffer reciveBuffer=ByteBuffer.allocate(BLOCK_BUFFER);
  private int flag=1;
  public NioServer(int port) throws IOException{
	  this.port=port;
      serverSocketChannel=ServerSocketChannel.open();
      serverSocketChannel.socket().bind(new InetSocketAddress(port));
      serverSocketChannel.configureBlocking(false);
      selector=Selector.open();
      serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
  }
  public void listen() throws IOException{
	  while(true){
		  selector.select();
		  Iterator<SelectionKey> keys=selector.selectedKeys().iterator();
		  while(keys.hasNext()){
			  SelectionKey key=keys.next();
			  process(key);
			  keys.remove();
		  }
	  }
  }
  private void process(SelectionKey key) throws IOException{
	  if(key.isAcceptable()){
		  ServerSocketChannel server=(ServerSocketChannel) key.channel();
		  SocketChannel client=server.accept();
		  client.configureBlocking(false);
		  client.register(selector, SelectionKey.OP_READ);
		  System.out.println("process acceptable......");
		  
	  }else if(key.isReadable()){
		  SocketChannel client=(SocketChannel) key.channel();
		  reciveBuffer.clear();
		  int count=client.read(reciveBuffer);
		  reciveBuffer.flip();
		  if(count>0){
			  String receiveTxt=new String(reciveBuffer.array(),0,count);
			  System.out.println((flag++)+" receive text is  "+receiveTxt);
			  //reciveBuffer.clear();
			  client.register(selector, SelectionKey.OP_WRITE);
		  }else if(count==0){
			  //client.close();
		  }else{
			  client.close();
		  }
	  }else if(key.isWritable()){
//		  使用Buffer读写数据一般遵循以下四个步骤：
//
//		  写入数据到Buffer
//		  调用flip()方法      lip()方法将Buffer从写模式切换到读模式
//		  从Buffer中读取数据
//		  调用clear()方法或者compact()方法  clear()方法会清空整个缓冲区。compact()方法只会清除已经读过的数据

		  SocketChannel client=(SocketChannel) key.channel();
          sendBuffer.clear();
		  sendBuffer.put(new String("to client"+(flag++)+" ").getBytes());
		  sendBuffer.flip();
		  client.write(sendBuffer);
		  client.register(selector, SelectionKey.OP_READ);

		 // client.close();
		  
	  }
	 
  }
  public static void main(String[] args) throws IOException{
	  NioServer server=new NioServer(8030);
	  server.listen();
  }
}
