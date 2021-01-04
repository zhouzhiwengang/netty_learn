package com.netty.server.two.handler;

import com.netty.protobuf.UserInfo;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

public class EchoServerHandler extends ChannelInboundHandlerAdapter {
	int count;

	//当通道激活时
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("连接的客户端地址:" + ctx.channel().remoteAddress());
        super.channelActive(ctx);
    }

	//当通道读到数据时
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
        	//拿到解码后的消息
        	UserInfo.UserMsg userMsg = (UserInfo.UserMsg) msg;
            System.out.println("Receive:[" + userMsg.toString() + "],count:[" + (++count) + "]");
            UserInfo.UserMsg.Builder builder =  userMsg.toBuilder().setState(1);
            ctx.writeAndFlush(builder);
        } finally {
            ReferenceCountUtil.release(msg);
        }


    }

	//当通道异常时
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
