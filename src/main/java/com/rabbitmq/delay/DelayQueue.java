package com.rabbitmq.delay;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

// 延时消费
public class DelayQueue {
    private static final String IP_ADDRESS = "127.0.0.1";
    private static final int PORT = 5672;//RabbitMQ（AMQP） 服务端默认端口号为 5672

    public static void main(String[] args) throws IOException,
            TimeoutException, InterruptedException {
        Address[] addresses = new Address[]{
                new Address(IP_ADDRESS, PORT)
        };

        ConnectionFactory factory = new ConnectionFactory();
        factory.setVirtualHost("/vhost_xyn");
        factory.setUsername("user_xyn");
        factory.setPassword("123456");
        Connection connection = factory.newConnection(addresses);
        final Channel channel = connection.createChannel();
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
        channel.basicConsume("queue.dlx", consumer);
    }
}
