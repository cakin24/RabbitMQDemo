package com.rabbitmq.util;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * 获取rabbitmq连接
 */
public class ConnectionUtils {

    public static Connection getConnection(String host, Integer port, String virtualHost,
                                           String username, String password) throws IOException, TimeoutException {
        // 连接工厂
        ConnectionFactory factory = new ConnectionFactory();
        // ip
        factory.setHost(host);
        // 端口号
        factory.setPort(port);
        // 虚拟主机
        factory.setVirtualHost(virtualHost);
        factory.setUsername(username);
        factory.setPassword(password);
        return factory.newConnection();
    }

    public static void close(Channel channel, Connection connection) throws IOException, TimeoutException {
        if (channel != null) {
            channel.close();
        }

        if (connection != null) {
            connection.close();
        }
    }
}
