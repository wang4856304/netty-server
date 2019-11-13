package com.wj.netty;

import com.wj.config.NettyConstants;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.bytes.ByteArrayEncoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

/**
 * @Author wangJun
 * @Description //TODO
 * @Date 2018/12/28
 **/


@Component
public class Server {
    private static Logger logger = LoggerFactory.getLogger(Server.class);

    @Value("${nettyServer.port}")
    private int port;

    @Resource
    private ServerHandler serverHandler;

    @Resource
    private AcceptorIdleStateTrigger acceptorIdleStateTrigger;

    public void start() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childOption(ChannelOption.TCP_NODELAY, true)//通过NoDelay禁用Nagle,使消息立即发出去，不用等待到一定的数据量才发出去
                    .childOption(ChannelOption.SO_KEEPALIVE, true)//保持长连接状态
                    .localAddress(port)
                    .childHandler(new ChannelInitializer<SocketChannel>() { // 绑定客户端连接时候触发操作
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            logger.info("来自客户端的连接, ip={}, port={}", ch.remoteAddress().getAddress().getHostAddress(), ch.remoteAddress().getPort());
                            ch.pipeline().addLast(new StringEncoder(Charset.forName("GBK")));//数据编码
                            ch.pipeline().addLast(new StringDecoder(Charset.forName("GBK")));//数据解码
                            ch.pipeline().addLast(new IdleStateHandler(
                                    NettyConstants.SERVER_READ_IDLE_TIME_OUT,
                                    NettyConstants.SERVER_WRITE_IDEL_TIME_OUT,
                                    NettyConstants.SERVER_ALL_IDEL_TIME_OUT,
                                    TimeUnit.SECONDS));
                            ch.pipeline().addLast(acceptorIdleStateTrigger); // 心跳检测,检测客户端是否断线
                            ch.pipeline().addLast(serverHandler); // 客户端触发操作
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
