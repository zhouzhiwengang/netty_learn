package com.netty.server.four.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ReConnectionHeartBeatHandler  extends SimpleChannelInboundHandler<String> {

	@Override
	protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
		/**
		 * msg 模拟:如何模拟心跳协议和消息协议,在msg 内容中包含逗号标识心跳协议,不包含内容标识普通消息
		 */
		if(msg.equalsIgnoreCase("heartbeat")){
			System.out.println("server 接收到心跳协议标识" + msg);
			 //服务端响应客户端心跳协议，返回"server ping" 标识服务器与客户端正常联通
            ctx.writeAndFlush("heartbeat");
		} else {
			System.out.print("server 接收信息:" + msg);
		}
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		// TODO Auto-generated method stub
		System.out.println("Established connection with the remote client.");
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		// TODO Auto-generated method stub
		 cause.printStackTrace();
	     ctx.close();
	}
	
	

}
