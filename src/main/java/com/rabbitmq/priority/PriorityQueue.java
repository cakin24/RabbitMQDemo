package com.rabbitmq.priority;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.util.ConnectionUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

// 消息优先级消费
public class PriorityQueue {
    private static final String IP_ADDRESS = "127.0.0.1";
    private static final int PORT = 5672;//RabbitMQ（AMQP） 服务端默认端口号为 5672

    public static void main(String[] args) throws IOException,
            TimeoutException, InterruptedException {

        Connection connection = ConnectionUtils.getConnection(IP_ADDRESS, PORT, "/vhost_xyn",
                "user_xyn", "123456");
        Channel channel = connection.createChannel();

        channel.exchangeDeclare("exchange.priority", "direct", true, false, null);
        Map<String, Object> arg = new HashMap<String, Object>(3);
        arg.put("x-max-priority", 10);
        channel.queueDeclare("queue.priority", true, false, false, arg);

        channel.queueBind("queue.priority", "exchange.priority", "routingKey");

        String message = "Hello World  10!";

        AMQP.BasicProperties.Builder builder = new AMQP.BasicProperties.Builder();
        builder.priority(10);
        AMQP.BasicProperties properties = builder.build();
        // 消息发布
        // 第三个参数为mandatory
        channel.basicPublish("exchange.priority", "routingKey", false,
                properties, message.getBytes());

        ConnectionUtils.close(channel, connection);
    }
}
