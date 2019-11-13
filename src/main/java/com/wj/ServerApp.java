package com.wj;

import com.wj.netty.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.Resource;

/**
 * @Author wangJun
 * @Description //TODO
 * @Date ${date} ${time}
 **/

@SpringBootApplication
public class ServerApp implements CommandLineRunner {
    private static Logger logger = LoggerFactory.getLogger(ServerApp.class);

    @Resource
    private Server server;

    public static void main(String args[]) {
        SpringApplication.run(ServerApp.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        logger.info("服务启动.......");
        server.start();
    }
}
