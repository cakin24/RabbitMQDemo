package com.rabbitmq.confirm;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.MessageProperties;
import com.rabbitmq.util.ConnectionUtils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

// 同步多条数据
public class Confirm {
    private static final String IP_ADDRESS = "127.0.0.1";
    private static final int PORT = 5672;//RabbitMQ（AMQP） 服务端默认端口号为 5672

    public static void main(String[] args) throws IOException, TimeoutException, InterruptedException {
        Connection connection = ConnectionUtils.getConnection(IP_ADDRESS, PORT, "/vhost_xyn",
                "user_xyn", "123456");
        Channel channel = connection.createChannel();

        channel.exchangeDeclare("exchange.confirm",  "direct", true, false, null);
        channel.queueDeclare("queue.confirm", true, false, false, null);
        channel.queueBind("queue.confirm", "exchange.confirm", "routingKey");

        channel.confirmSelect();

        String msg = "我是confirm模式 消息 批量发送";
        //多条一起发送
        for (int i = 0; i < 5; i++) {
            channel.basicPublish("exchange.confirm", "queue.confirm", MessageProperties.PERSISTENT_TEXT_PLAIN, msg.getBytes());
        }
        try {

            //等待回复，如果回复true
            if (channel.waitForConfirms()) {
                System.out.println("发送成功");
            }
            else {
                System.out.println("发送失败");
            }
        }
        catch (InterruptedException e) {
            e.printStackTrace();
            System.out.println("发送失败");
        }
        channel.close();
        connection.close();
    }


}
