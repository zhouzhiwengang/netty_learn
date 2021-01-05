package com.netty.client.four;

import java.util.concurrent.TimeUnit;
import com.netty.client.four.handler.CommonHandler;
import com.netty.client.four.handler.ReconnectionHeartBeatHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;

public class ReconnectionHeartBeatClient {
	int port = 8083;
	public void run() {
		EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
		try {
			// 首先，netty通过Bootstrap启动客户端
			Bootstrap bootstrap = new Bootstrap();
			// 第1步 定义线程组，处理读写和链接事件，没有了accept事件
			bootstrap.group(eventLoopGroup)
			  		// 第2步 绑定客户端通道
					.channel(NioSocketChannel.class)
					.option(ChannelOption.SO_KEEPALIVE, true)
		            .option(ChannelOption.TCP_NODELAY, true)
					 // 第3步 给NIoSocketChannel初始化handler， 处理读写事件
					.handler(new ChannelInitializer<Channel>() {
						@Override
						protected void initChannel(Channel channel) throws Exception {
							ChannelPipeline pipeline = channel.pipeline();
							pipeline.addLast("decoder", new StringDecoder());
							pipeline.addLast("encoder", new StringEncoder());
							// 心跳协议发送
							pipeline.addLast(new IdleStateHandler(0, 6, 0, TimeUnit.SECONDS));
							// 心跳协议自定义处理逻辑
							pipeline.addLast(new ReconnectionHeartBeatHandler());
							// 普通信息处理
							pipeline.addLast(new CommonHandler());
						}
		
					});

			 // 连接服务端
			ChannelFuture channelFuture = bootstrap.connect("localhost", port).sync();
			 //客户端断线重连逻辑
			channelFuture.addListener((ChannelFutureListener) future -> {
	            if (future.isSuccess()) {
	                System.out.println("连接Netty服务端成功");
	            } else {
	                System.out.println("连接失败，进行断线重连");
	                future.channel().eventLoop().schedule(() -> run(), 20, TimeUnit.SECONDS);
	            }
	        });
			// 客户端连接服务端通道,可以进行消息发送
			channelFuture.channel().writeAndFlush("Hello Server, I'm online");
			channelFuture.channel().closeFuture().sync();
		} catch (Exception e) {
			// do something
		} finally {
			eventLoopGroup.shutdownGracefully();
		}
		
	}
	public static void main(String[] args) {
		new ReconnectionHeartBeatClient().run();
	}
}
