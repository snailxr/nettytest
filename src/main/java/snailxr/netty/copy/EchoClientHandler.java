package snailxr.netty.copy;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class EchoClientHandler extends ChannelHandlerAdapter {
	private int counter;

	@Override
	public void channelActive(ChannelHandlerContext ctx) {
		System.out.println("active..............");
		for (int i = 0; i < 10; i++) {
			ctx.writeAndFlush(Unpooled.copiedBuffer("hi,welcome to netty $_".getBytes()));
		}
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		System.out.println("this is " + ++counter + " times receice  server :" + msg);
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {
		ctx.flush();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		ctx.close();
	}
}
