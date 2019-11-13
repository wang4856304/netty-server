package com.wj.netty;

import com.wj.service.MessageHandlerService;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.SocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author wangJun
 * @Description //TODO
 * @Date 2018.12.24
 **/

@Component
@ChannelHandler.Sharable
public class ServerHandler extends ChannelInboundHandlerAdapter {

    private static Logger logger = LoggerFactory.getLogger(ServerHandler.class);

    public static Map<String, Channel> CHANNEL_MAP = new ConcurrentHashMap<>();

    @Resource
    private MessageHandlerService messageHandlerService;


    /*
     * channelAction
     *
     * channel 通道 action 活跃的
     *
     * 当客户端主动链接服务端的链接后，这个通道就是活跃的了。也就是客户端与服务端建立了通信通道并且可以传输数据
     *
     */
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        SocketChannel socketChannel = (SocketChannel)ctx.channel();
        String clientIp = socketChannel.remoteAddress().getAddress().getHostAddress();
        logger.info(clientIp + " 通道已激活！");
        String clientId = ctx.channel().id().asLongText();
        CHANNEL_MAP.put(clientId, ctx.channel());
    }

    /*
     * channelInactive
     *
     * channel 通道 Inactive 不活跃的
     *
     * 当客户端主动断开服务端的链接后，这个通道就是不活跃的。也就是说客户端与服务端的关闭了通信通道并且不可以传输数据
     *
     */
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        SocketChannel socketChannel = (SocketChannel)ctx.channel();
        String clientIp = socketChannel.remoteAddress().getAddress().getHostAddress();
        logger.warn(clientIp + " 设备连接已断开！");
        String clientId = ctx.channel().id().asLongText();
        CHANNEL_MAP.remove(clientId);
        // 关闭流

    }

    /**
     * 功能：读取服务器发送过来的信息
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg == null || msg.toString().length() == 0) {
            logger.warn("recv msg is empty");
            return;
        }
        String rev = msg.toString();
        SocketChannel socketChannel = (SocketChannel)ctx.channel();
        String clientIp = socketChannel.remoteAddress().getAddress().getHostAddress();
        String clientId = ctx.channel().id().asLongText();
        messageHandlerService.handlerMsg(clientIp, clientId, rev);
        logger.info("服务器收到客户端" + clientIp + "数据:" + rev);

    }

    /**
     * 功能：读取完毕客户端发送过来的数据之后的操作
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        //logger.info("服务端接收数据完毕..");
        // 第一种方法：写一个空的buf，并刷新写出区域。完成后关闭sock channel连接。
        //ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        // ctx.flush();
        // ctx.flush(); //
        // 第二种方法：在client端关闭channel连接，这样的话，会触发两次channelReadComplete方法。
        // ctx.flush().close().sync(); // 第三种：改成这种写法也可以，但是这中写法，没有第一种方法的好。
    }

    /**
     * 功能：服务端发生异常的操作
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        SocketChannel socketChannel = (SocketChannel)ctx.channel();
        String clientIp = socketChannel.remoteAddress().getAddress().getHostAddress();
        String clientId = ctx.channel().id().asLongText();
        logger.error("clientIp={}, clientId={}", clientIp, clientId, cause);
        //ctx.close();//关闭通道
    }
}
