package com.rabbitmq.transaction;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.MessageProperties;
import com.rabbitmq.util.ConnectionUtils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

// 事务
public class Transaction {
    private static final String EXCHANGE_NAME = "exchange_demo";
    private static final String ROUTING_KEY = "routingkey_demo";
    private static final String QUEUE_NAME = "msgTTL1_demo";
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
        channel.queueDeclare(QUEUE_NAME, true, false, false, null);
        // 将交换器与队列通过路由键绑定
        channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, ROUTING_KEY);

        // 开启事务
        try {
            channel.txSelect();
            channel.basicPublish(EXCHANGE_NAME, ROUTING_KEY, false,
                    MessageProperties.PERSISTENT_TEXT_PLAIN, "事务".getBytes());
            channel.txCommit();
        } catch (IOException e) {
            e.printStackTrace();
            channel.txRollback();
        }

        ConnectionUtils.close(channel, connection);
    }
}
