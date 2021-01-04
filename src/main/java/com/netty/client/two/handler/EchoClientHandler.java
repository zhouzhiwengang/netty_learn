package com.netty.client.two.handler;

import com.netty.protobuf.UserInfo;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

public class EchoClientHandler extends ChannelInboundHandlerAdapter {
	int count;
    String echo_req = "Hi,zhouchengxi,welcome to netty";
    UserInfo.UserMsg.Builder builder = UserInfo.UserMsg.newBuilder();

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        for (int i = 1; i <= 10; i++) {
            UserInfo.UserMsg fangyan = builder.setId(1).setAge(i).setName("zhouchengxi" + i).build();
            ctx.writeAndFlush(fangyan);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            UserInfo.UserMsg userMsg = (UserInfo.UserMsg) msg;
            System.out.println("Server:[" + userMsg.toString() + "]count:[" + (++count) + "]");
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
