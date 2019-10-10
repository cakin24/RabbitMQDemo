package com.rabbitmq.confirm;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmListener;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.MessageProperties;
import com.rabbitmq.util.ConnectionUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.TimeoutException;

// 异步confirm
public class Confirm2 {
    private static final String IP_ADDRESS = "127.0.0.1";
    private static final int PORT = 5672;//RabbitMQ（AMQP） 服务端默认端口号为 5672

    public static void main(String[] args) throws IOException, TimeoutException {
        Connection connection = ConnectionUtils.getConnection(IP_ADDRESS, PORT, "/vhost_xyn",
                "user_xyn", "123456");
        Channel channel = connection.createChannel();

        channel.exchangeDeclare("exchange.confirm", "direct", true, false, null);
        channel.queueDeclare("queue.confirm2", true, false, false, null);
        channel.queueBind("queue.confirm2", "exchange.confirm", "routingKey2");

        final SortedSet<Long> confirmSet = Collections.synchronizedNavigableSet(new TreeSet<Long>());
        channel.confirmSelect();

        // 开启监听
        channel.addConfirmListener(new ConfirmListener() {
            // 处理成功
            public void handleAck(long deliveryTag, boolean multiple) throws IOException {
                System.out.println("消息发送成功: " + deliveryTag
                        + ", multiple : " + multiple);
                if (multiple) {
                    confirmSet.headSet(deliveryTag - 1).clear();
                } else {
                    confirmSet.remove(deliveryTag);
                }
            }

            // 处理失败
            public void handleNack(long deliveryTag, boolean multiple) throws IOException {
                if (multiple) {
                    confirmSet.headSet(deliveryTag - 1).clear();
                } else {
                    confirmSet.remove(deliveryTag);
                }
            }
        });

        // 循环发送消息
        for (int i = 0; true; i++) {
            String msg = "我是confirm模式 消息 异步【" + i + "】";
            long tag = channel.getNextPublishSeqNo();
            //发送消息
            channel.basicPublish("exchange.confirm", "queue.confirm2", MessageProperties.PERSISTENT_TEXT_PLAIN, msg.getBytes());
            System.out.println("tag:" + tag);
            confirmSet.add(tag);
        }
//        channel.close();
//        connection.close();
    }


}
