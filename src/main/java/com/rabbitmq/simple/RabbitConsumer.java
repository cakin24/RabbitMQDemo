package com.rabbitmq.simple;

import com.rabbitmq.client.*;
import com.rabbitmq.util.ConnectionUtils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 消费者
 */
public class RabbitConsumer {
    private static final String QUEUE_NAME = "queue_demo";
    private static final String IP_ADDRESS = "127.0.0.1";
    private static final int PORT = 5672;

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        Address[] addresses = new Address[]{
                new Address(IP_ADDRESS, PORT)
        };

        ConnectionFactory factory = new ConnectionFactory();
        factory.setVirtualHost("/vhost_xyn");
        factory.setUsername("user_xyn");
        factory.setPassword("123456");
        Connection connection = factory.newConnection(addresses);
        final Channel channel = connection.createChannel();
        // 设置客户端最多未被ack的个数
        channel.basicQos(64);
        Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag,
                                       Envelope envelope,
                                       AMQP.BasicProperties properties,
                                       byte[] body)
                    throws IOException {
                System.out.println(" recv message: " + new String(body));
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                channel.basicAck(envelope.getDeliveryTag(), false);
            }
        };
        channel.basicConsume(QUEUE_NAME, consumer);
        // 等待回调函数执行完毕之后 关闭资源
        TimeUnit.SECONDS.sleep(5);
        ConnectionUtils.close(channel, connection);
    }
}