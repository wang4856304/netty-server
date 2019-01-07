package com.wj.service.impl;

import com.wj.ServerHandler;
import com.wj.service.MessageHandlerService;
import org.springframework.stereotype.Service;

/**
 * @Author wangJun
 * @Description //TODO
 * @Date ${date} ${time}
 **/

@Service
public class MessageHandlerServiceImpl implements MessageHandlerService {
    @Override
    public void handlerMsg(String ip, String clientId, String msg) {
        ServerHandler.CHANNEL_MAP.get(clientId).writeAndFlush("hello world");
    }
}
