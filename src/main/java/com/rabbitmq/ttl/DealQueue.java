package com.rabbitmq.ttl;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.MessageProperties;
import com.rabbitmq.util.ConnectionUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * 死信队列（路由键为rk的消息过期后被放置到死信队列）
 */
public class DealQueue {
    private static final String IP_ADDRESS = "127.0.0.1";
    private static final int PORT = 5672;//RabbitMQ（AMQP） 服务端默认端口号为 5672

    public static void main(String[] args) throws IOException,
            TimeoutException, InterruptedException {

        Connection connection = ConnectionUtils.getConnection(IP_ADDRESS, PORT, "/vhost_xyn",
                "user_xyn", "123456");
        Channel channel = connection.createChannel();

        channel.exchangeDeclare("exchange.dlx", "direct", true, false, null);
        channel.exchangeDeclare("exchange.normal", "fanout", true, false, null);

        Map<String, Object> arg = new HashMap<String, Object>(3);
        arg.put("x-message-ttl", 3000);
        arg.put("x-dead-letter-exchange", "exchange.dlx");

        channel.queueDeclare("queue.normal", false, false, false, arg);
        channel.queueBind("queue.normal", "exchange.normal", "");

        channel.queueDeclare("queue.dlx", false, false, false, null);
        channel.queueBind("queue.dlx", "exchange.dlx", "rk");

        String message = "delay message !";

        channel.basicPublish("exchange.normal", "rk", false,
                MessageProperties.PERSISTENT_TEXT_PLAIN,
                message.getBytes());

        ConnectionUtils.close(channel, connection);
    }
}
