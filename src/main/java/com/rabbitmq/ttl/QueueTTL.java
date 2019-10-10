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
 * 队列过期
 */
public class QueueTTL {
    private static final String EXCHANGE_NAME = "exchange_demo";
    private static final String ROUTING_KEY = "routingkey_demo";
    private static final String QUEUE_NAME = "QueueTTL_demo";
    private static final String IP_ADDRESS = "127.0.0.1";
    private static final int PORT = 5672;//RabbitMQ（AMQP） 服务端默认端口号为 5672

    public static void main(String[] args) throws IOException,
            TimeoutException, InterruptedException {

        Connection connection = ConnectionUtils.getConnection(IP_ADDRESS, PORT, "/vhost_xyn",
                "user_xyn", "123456");
        Channel channel = connection.createChannel();
        // 创建一个 type="direct" 、持久化的、非自动删除的交换器
        channel.exchangeDeclare(EXCHANGE_NAME, "direct", true, false, null);
        // 创建一个持久化、非排他的、非自动删除的队列
        Map<String, Object> arg = new HashMap<String, Object>(3);
        // 设置队列过期时间，单位：毫秒
        arg.put("x-expires", 10000);
        channel.queueDeclare(QUEUE_NAME, true, false, false, arg);
        // 将交换器与队列通过路由键绑定
        channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, ROUTING_KEY);
        // 发送一条持久化的消息: hello world !
        String message = "Hello World !";
        // 消息发布
        // 第三个参数为mandatory
        channel.basicPublish(EXCHANGE_NAME, ROUTING_KEY, false,
                MessageProperties.PERSISTENT_TEXT_PLAIN,
                message.getBytes());

        ConnectionUtils.close(channel, connection);
    }
}
