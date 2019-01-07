package com.wj;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Author wangJun
 * @Description //TODO
 * @Date ${date} ${time}
 **/

@SpringBootApplication
public class ServerApp implements CommandLineRunner {
    private static Logger logger = LoggerFactory.getLogger(ServerApp.class);

    public static void main(String args[]) {
        SpringApplication.run(ServerApp.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        Server server = new Server(9999);
        server.start();
    }
}
