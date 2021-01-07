package com.netty.server.four.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

public class ServerIdleStateTrigger extends ChannelInboundHandlerAdapter  {

	/**
	 * 如果5s没有读请求，则向客户端发送心跳
	 */
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		// TODO Auto-generated method stub
		if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.READER_IDLE) {
            	ctx.writeAndFlush("heartbeat");
            }
        } 
        super.userEventTriggered(ctx, evt);
	}
	
}
