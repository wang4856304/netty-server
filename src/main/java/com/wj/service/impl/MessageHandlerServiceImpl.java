package com.wj.service.impl;

import com.wj.netty.ServerHandler;
import com.wj.service.MessageHandlerService;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * @Author wangJun
 * @Description //TODO
 * @Date 2018.12.24
 **/

@Service
public class MessageHandlerServiceImpl implements MessageHandlerService {

    private Logger logger = LoggerFactory.getLogger(MessageHandlerServiceImpl.class);

    @Override
    public void handlerMsg(String ip, String clientId, String msg) {

        //TODO 线程异步处理可增加吞吐量
        //业务逻辑
        String result = getResult(msg);

        //处理完成业务，向客户端推送信息
        Channel channel = ServerHandler.CHANNEL_MAP.get(clientId);
        if (channel != null) {
            logger.info("send data:{}", "hello world");
            channel.writeAndFlush("hello world");
        }
        else {
            logger.error("invalid channel");
        }
    }

    /**
     * 根据不同的消息内容处理不同的业务逻辑
     * @param msg
     * @return
     */
    private String getResult(String msg) {

        return null;
    }
}
