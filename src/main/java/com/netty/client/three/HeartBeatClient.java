package com.netty.client.three;

import java.util.Random;

import com.netty.client.three.handler.HeartBeatClientHandler;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class HeartBeatClient {
	public static void main(String[] args) {
		int port = 8082;
		EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
		try {
			Bootstrap bootstrap = new Bootstrap();
			bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class).handler(new ChannelInitializer<Channel>() {

				@Override
				protected void initChannel(Channel channel) throws Exception {
					ChannelPipeline pipeline = channel.pipeline();
					pipeline.addLast("decoder", new StringDecoder());
					pipeline.addLast("encoder", new StringEncoder());
					pipeline.addLast(new HeartBeatClientHandler());
				}

			});

			Channel channel = bootstrap.connect("localhost", port).sync().channel();
			String text = "I am alive";
			while (channel.isActive()) {
				int num = new Random().nextInt(10);
				Thread.sleep(num * 1000);
				channel.writeAndFlush(text);
			}
		} catch (Exception e) {
			// do something
		} finally {
			eventLoopGroup.shutdownGracefully();
		}
	}
}
