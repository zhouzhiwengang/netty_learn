package com.netty.server.four;

import java.util.concurrent.TimeUnit;

import com.netty.server.four.handler.ReConnectionHeartBeatHandler;
import com.netty.server.four.handler.ServerIdleStateTrigger;
import com.netty.server.three.handler.HeartBeatHandler;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;

public class ReConnectionHeartBeatServer {
	private int port = 8083;
	 /**
     * 设置空闲检测时间为 30s
     */
    private static final int READER_IDLE_TIME = 5;
    
	private void run() {
		// 首先，netty通过ServerBootstrap启动服务端
				ServerBootstrap bootstrap = new ServerBootstrap();
				//第1步定义两个线程组，用来处理客户端通道的accept和读写事件
		        //parentGroup用来处理accept事件，childgroup用来处理通道的读写事件
		        //parentGroup获取客户端连接，连接接收到之后再将连接转发给childgroup去处理
				EventLoopGroup boss = new NioEventLoopGroup();
				EventLoopGroup worker = new NioEventLoopGroup();
				try {
					bootstrap.group(boss, worker)
							.handler(new LoggingHandler(LogLevel.INFO))
							 //用于构造服务端套接字ServerSocket对象，标识当服务器请求处理线程全满时，用于临时存放已完成三次握手的请求的队列的最大长度。
					         //用来初始化服务端可连接队列
					         //服务端处理客户端连接请求是按顺序处理的，所以同一时间只能处理一个客户端连接，多个客户端来的时候，服务端将不能处理的客户端连接请求放在队列中等待处理，backlog参数指定了队列的大小。
							.option(ChannelOption.SO_BACKLOG, 128)
							//设置TCP长连接,一般如果两个小时内没有数据的通信时,TCP会自动发送一个活动探测数据报文
			                .childOption(ChannelOption.SO_KEEPALIVE, true)
			                //将小的数据包包装成更大的帧进行传送，提高网络的负载
			                .childOption(ChannelOption.TCP_NODELAY, true)
							  //第2步绑定服务端通道
							.channel(NioServerSocketChannel.class)
							 //第3步绑定handler，处理读写事件，ChannelInitializer是给通道初始化
							.childHandler(new ChannelInitializer<SocketChannel>() {

								@Override
								protected void initChannel(SocketChannel channel) throws Exception {
									ChannelPipeline pipeline = channel.pipeline();
									pipeline.addLast("decoder", new StringDecoder());
									pipeline.addLast("encoder", new StringEncoder());
									pipeline.addLast(new IdleStateHandler(READER_IDLE_TIME, 0, 0, TimeUnit.SECONDS));
									pipeline.addLast(new ServerIdleStateTrigger());
									pipeline.addLast(new ReConnectionHeartBeatHandler());

								}

							});
					 //第4步绑定8083端口
					ChannelFuture future = bootstrap.bind(port).sync();
					//当通道关闭了，就继续往下走
					future.channel().closeFuture().sync();
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					worker.shutdownGracefully();
					boss.shutdownGracefully();
				}

	}
	public static void main(String[] args) {
		new ReConnectionHeartBeatServer().run();
	}
}
