package com.rabbitmq;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.util.ConnectionUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * 备用交换器
 */
public class AlternateExchange {
    private static final String IP_ADDRESS = "127.0.0.1";
    private static final int PORT = 5672;//RabbitMQ（AMQP） 服务端默认端口号为 5672

    public static void main(String[] args) throws IOException,
            TimeoutException, InterruptedException {

        Connection connection = ConnectionUtils.getConnection(IP_ADDRESS, PORT, "/vhost_xyn",
                "user_xyn", "123456");
        Channel channel = connection.createChannel();

        Map<String, Object> arg = new HashMap<String, Object>(3);
        arg.put("alternate-exchange", "myAe");

        // 创建正常交换器
        channel.exchangeDeclare("normalExchange", "direct", true, false, false, arg);
        channel.exchangeDeclare("myAe", "fanout", true, false, false, null);

        channel.queueDeclare("normalQueue", true, false, false, null);
        channel.queueBind("normalQueue", "normalExchange", "normalKey");

        channel.queueDeclare("unroutingQueue", true, false, false, null);
        channel.queueBind("unroutingQueue", "myAe", "");

        String message = "hello test";

        channel.basicPublish("normalExchange", "123123",
                                null, message.getBytes());
        ConnectionUtils.close(channel, connection);
    }
}
