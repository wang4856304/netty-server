package com.wj;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Author wangJun
 * @Description //TODO
 * @Date ${date} ${time}
 **/
public class ServerApp {
    private static Logger logger = LoggerFactory.getLogger(ServerApp.class);

    public static void main(String args[]) {
        Server server = new Server(9999);
        server.start();
    }
}
