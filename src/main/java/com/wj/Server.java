package com.wj;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.codec.string.StringEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

/**
 * @Author wangJun
 * @Description //TODO
 * @Date 2018/12/28
 **/
public class Server {
    private static Logger logger = LoggerFactory.getLogger(Server.class);

    private int port;

    public void setPort(int port) {
        this.port = port;
    }

    public Server(int port) {
        this.port = port;
    }

    public void start() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .option(ChannelOption.TCP_NODELAY, true)//通过NoDelay禁用Nagle,使消息立即发出去，不用等待到一定的数据量才发出去
                    .childOption(ChannelOption.SO_KEEPALIVE, true)//保持长连接状态
                    .localAddress(port)
                    .childHandler(new ChannelInitializer<SocketChannel>() { // 绑定客户端连接时候触发操作
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            logger.info("来自客户端的连接, ip={}, port={}", ch.remoteAddress().getAddress().getHostAddress(), ch.remoteAddress().getPort());
                            ch.pipeline().addLast(new StringEncoder(Charset.forName("GBK")));
                            ch.pipeline().addLast(new ServerHandler()); // 客户端触发操作
                            ch.pipeline().addLast(new ByteArrayEncoder());
                        }
                    });
            ChannelFuture cf = b.bind().sync(); // 服务器异步创建绑定
            InetSocketAddress inetSocketAddress = (InetSocketAddress)cf.channel().localAddress();
            logger.info("服务器启动监听, ip={}", cf.channel().localAddress());
            cf.channel().closeFuture().sync(); // 关闭服务器通道
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
