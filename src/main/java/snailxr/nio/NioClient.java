package snailxr.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by snailxr on 15/11/22.
 */
public class NioClient {
    private static int flag=1;
    private static int blockSize =32;
    private static ByteBuffer sendBuffer=ByteBuffer.allocate(blockSize);
    private static  ByteBuffer receiveBuffer=ByteBuffer.allocate(blockSize);
    private final static InetSocketAddress serverAddress=new InetSocketAddress("127.0.0.1",9090);
    public static void main(String[] args) throws IOException {
        System.out.println("........");
        SocketChannel socketChannel=SocketChannel.open();
        socketChannel.configureBlocking(false);
        Selector selector=Selector.open();
        socketChannel.register(selector, SelectionKey.OP_CONNECT);
        socketChannel.connect(serverAddress);
        Set<SelectionKey> selectionKeys;
        Iterator<SelectionKey>iterator;
        SelectionKey selectionKey;
        SocketChannel client;
        String sendText;
        String receiveText;
        int count;
        while (true){
            selector.select();
            //System.out.println(flag++);
            selectionKeys=selector.selectedKeys();
            iterator=selectionKeys.iterator();
            //System.out.println(selectionKeys);
            while (iterator.hasNext()){
                selectionKey=iterator.next();
                if(selectionKey.isConnectable()){
                    System.out.println("client connect....");
                    client=(SocketChannel)selectionKey.channel();
                    if(client.isConnectionPending()){
                        client.finishConnect();
                        System.out.println("client connect finished...");
                        sendBuffer.clear();
                        sendBuffer.put("hello server".getBytes());
                        sendBuffer.flip();
                        client.write(sendBuffer);
                    }
                    client.register(selector,SelectionKey.OP_READ);
                }else if(selectionKey.isReadable()){
                    client=(SocketChannel)selectionKey.channel();
                    receiveBuffer.clear();
                    count=client.read(receiveBuffer);
                    if(count>0){
                    	receiveBuffer.flip();

                        receiveText=new String(receiveBuffer.array(),0,count);
                        System.out.println(receiveText);
                       receiveBuffer.clear();
                        //client.register(selector,SelectionKey.OP_WRITE);
                       selectionKey.interestOps(SelectionKey.OP_WRITE);
                    }else if(count==0){
                    	
                    }else{
                    	client.close();
                    }
                }else if(selectionKey.isWritable()){
                    client=(SocketChannel)selectionKey.channel();
                    sendBuffer.clear();
                    sendText="text from client"+flag++;
                    sendBuffer.put(sendText.getBytes());
                    sendBuffer.flip();
                    client.write(sendBuffer);
                    //client.register(selector,SelectionKey.OP_READ);
                    selectionKey.interestOps(SelectionKey.OP_READ);
                }
                iterator.remove();
            }
            selectionKeys.clear();
        }
    }
}
