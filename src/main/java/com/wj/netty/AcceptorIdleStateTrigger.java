package com.wj.netty;

import com.wj.config.NettyConstants;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@ChannelHandler.Sharable
@Component
public class AcceptorIdleStateTrigger extends ChannelInboundHandlerAdapter {

    private Logger logger = LoggerFactory.getLogger(AcceptorIdleStateTrigger.class);

    private static Map<String, Integer> CHANNEL_OVER_TIMES_MAP = new ConcurrentHashMap<>();

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

        String clientId = ctx.channel().id().asLongText();
        if(clientId == null || clientId.length() == 0){
            return;
        }

        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent) evt;
            IdleState state=event.state();
            if (state==IdleState.READER_IDLE) {

                SocketChannel socketChannel = (SocketChannel)ctx.channel();
                String clientIp = socketChannel.remoteAddress().getAddress().getHostAddress();

                Integer overTimes = CHANNEL_OVER_TIMES_MAP.get(clientId);
                if (overTimes == null) {
                    ticket(ctx, clientId, 0);
                }
                else {
                    if (overTimes < NettyConstants.MAX_OVER_TIMES) {
                        ticket(ctx, clientId, overTimes);
                    }
                    else {
                        logger.error(clientIp + " 设备重连失败，关闭通道");
                        //TODO 报警通知
                        ctx.close();
                    }
                }
            }
        }
    }

    /**
     * 向客户端发送心跳包
     * @param ctx
     * @param clientId
     */
    private void ticket(ChannelHandlerContext ctx, String clientId, int overTimes) {
        ChannelFuture future = ctx.channel().writeAndFlush("ping");
        SocketChannel socketChannel = (SocketChannel)ctx.channel();
        String clientIp = socketChannel.remoteAddress().getAddress().getHostAddress();
        logger.info(clientIp + " 设备连接已断开,正在进行重连......");
        future.addListener((ChannelFutureListener) future1 -> {
            // 检查操作的状态
            if (future1.isSuccess()) {
                logger.info("clientIp={},重连成功", clientIp);
                CHANNEL_OVER_TIMES_MAP.put(clientId, 0);//重置重连次数为0
            } else {
                int times = overTimes + 1;
                CHANNEL_OVER_TIMES_MAP.put(clientId, times);
            }
        });
    }
}
