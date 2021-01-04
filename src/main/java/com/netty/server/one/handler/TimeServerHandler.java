package com.netty.server.one.handler;

import java.time.LocalDateTime;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class TimeServerHandler extends ChannelInboundHandlerAdapter {

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		String request = (String) msg; // 2
		String response = null;
		if ("QUERY TIME ORDER".equals(request)) { // 3
			response = LocalDateTime.now().toString();
		} else {
			response = "BAD REQUEST";
		}
		response += System.getProperty("line.separator"); // 4
		ByteBuf resp = Unpooled.copiedBuffer(response.getBytes()); // 5
		ctx.writeAndFlush(resp); // 6

	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		ctx.close(); //7
	}

}
