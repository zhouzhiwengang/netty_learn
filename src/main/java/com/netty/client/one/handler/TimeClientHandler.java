package com.netty.client.one.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class TimeClientHandler extends ChannelInboundHandlerAdapter {
	private byte[] req=("QUERY TIME ORDER" + System.getProperty("line.separator")).getBytes();
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		 ByteBuf message = Unpooled.buffer(req.length);
	     message.writeBytes(req);
	     ctx.writeAndFlush(message);
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		String body = (String) msg;
        System.out.println("Now is:" + body);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		ctx.close();
	}

}
