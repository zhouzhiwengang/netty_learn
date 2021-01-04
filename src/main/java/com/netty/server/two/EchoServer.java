package com.netty.server.two;

import com.netty.protobuf.UserInfo;
import com.netty.server.two.handler.EchoServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class EchoServer {
	private int port=8081;
	private void run() {
		  EventLoopGroup bossGroup = new NioEventLoopGroup();
	      EventLoopGroup workerGroup = new NioEventLoopGroup();
	        try {
	            //创建ServerBootstrap对象，它是Netty用于启动NIO服务端的辅助启动类，目的是降低服务端的开发复杂度。
	            ServerBootstrap bootstrap = new ServerBootstrap();

	            bootstrap
	                    //调用ServerBootstrap的group方法，将两个NIO线程组当作入参传递到ServerBootstrap中。
	                    .group(bossGroup, workerGroup)
	                    //接着设置创建的Channel为NioServerSocketChannel,它的功能对应于JDK NIO类库中的ServerSocketChannel类。
	                    .channel(NioServerSocketChannel.class)
	                    .handler(new LoggingHandler(LogLevel.INFO))
	                    //最后绑定I/O事件的处理类ChannelInitializer，它的作用类似于Reactor模式中的Handler类，
	                    //主要用于处理网络I/O事件，例如记录日志、对消息进行编解码等。
	                    .childHandler(new ChannelInitializer<SocketChannel>() {
	                        @Override
	                        protected void initChannel(SocketChannel socketChannel) throws Exception {
	                            socketChannel.pipeline()
	                                    //创建消息解码器，获取消息长度
	                                    .addLast(new ProtobufVarint32FrameDecoder())
	                                    //指定消息类型
	                                    .addLast(new ProtobufDecoder(UserInfo.UserMsg.getDefaultInstance()))
	                                    //消息头置长度
	                                    .addLast(new ProtobufVarint32LengthFieldPrepender())
	                                    //创建消息编码器
	                                    .addLast(new ProtobufEncoder())
	                                    //消息的处理
	                                    .addLast(new EchoServerHandler());
	                        }
	                    });
	            //服务端启动辅助类配置完成之后，调用它的bind 方法绑定监听端口，
	            //随后，调用它的同步阻塞方法sync等待绑定操作完成。
	            //完成之后Netty会返回一个ChannelFuture, 它的功能类似于JDK的java.util.concurrent.Future，主要用于异步操作的通知回调。
	            //使用f.channel.closeFuture().syncO方法进行阻塞，等待服务端链路关闭之后main函数才退出。
	            ChannelFuture sync = bootstrap.bind(port).sync();

	            sync.channel().closeFuture().sync();
	        } catch (Exception e) {
	            e.printStackTrace();
	        } finally {
	            //调用NIO线程组的shutdownGracefully 进行优雅退出，它会释放跟shutdownGracefully相关联的资源。
	            bossGroup.shutdownGracefully();
	            workerGroup.shutdownGracefully();
	        }

	}
	
	public static void main(String[] args) {
		new EchoServer().run();
	}
}
