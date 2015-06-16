package snailxr.netty.copy;

import java.net.SocketAddress;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

public class EchoServerHandler extends ChannelHandlerAdapter {
	int counter = 0;

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object arg1) throws Exception {

		String body = (String) arg1;
		System.out.println("this is " + ++counter + "times receive client:" + body);
		body += "$_";
		ByteBuf echo = Unpooled.copiedBuffer(body.getBytes());
		ctx.writeAndFlush(echo);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext arg0, Throwable arg1) throws Exception {

		arg1.printStackTrace();
		arg0.close();
	}

}
