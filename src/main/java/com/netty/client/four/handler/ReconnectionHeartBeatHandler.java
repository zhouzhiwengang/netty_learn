package com.netty.client.four.handler;

import java.util.concurrent.TimeUnit;
import com.netty.client.four.ReconnectionHeartBeatClient;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.EventLoop;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class ReconnectionHeartBeatHandler extends ChannelInboundHandlerAdapter {


	/**
	 * 如果4s没有收到写请求，则向服务端发送心跳请求
	 */
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		if (evt instanceof IdleStateEvent) {
			IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
			// 客户端写入空闲，向服务器发送心跳包
			if (idleStateEvent.state() == IdleState.WRITER_IDLE) {
				// 向服务端送心跳包
				String message = "heartbeat";
				// 发送心跳消息，并在发送失败时关闭该连接
				ctx.writeAndFlush(message);
			}
		} 
		super.userEventTriggered(ctx, evt);
		
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("Client is close");
		// 如果运行过程中服务端挂了,执行重连机制
		EventLoop eventLoop = ctx.channel().eventLoop();
		eventLoop.schedule(() -> new ReconnectionHeartBeatClient().run(), 10L, TimeUnit.SECONDS);
		super.channelInactive(ctx);
	}

}
