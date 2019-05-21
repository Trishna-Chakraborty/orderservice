package com.example.orderservice;


import com.rabbitmq.client.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class OrderService {
    @Autowired
    OrderRepository orderRepository;


    public  boolean setOrder (String value){
        if(value.equals("true")){
            OrderEntity orderEntity = new OrderEntity();
            orderEntity.setId(UUID.randomUUID());
            orderEntity.setOrderState(OrderState.APPROVED);
            orderRepository.save(orderEntity);
            return true;
        }

       return false;
    }


    public void consume(String name) throws Exception{

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection=factory.newConnection();
        Channel channel=connection.createChannel();
        //channel.queueDeclare(name, false, false, false, null);


        channel.exchangeDeclare("dead_exchange", "direct");
        channel.queueDeclare("dead_queue", false, false, false, null);
        channel.queueBind("dead_queue", "dead_exchange", "");


        Map<String, Object> args = new HashMap<String, Object>();
        args.put("x-dead-letter-exchange", "dead_exchange");
        args.put("x-message-ttl", 60000);
        channel.queueDeclare(name, false, false, false, args);
        channel.exchangeDeclare(name+"_exchange", "direct");;
        channel.queueBind(name, name+"_exchange", "");
        channel.basicQos(1);
        System.out.println(" [x] Awaiting RPC requests from " + name);

        Object monitor = new Object();
        DeliverCallback deliverCallback = (consumerTag, delivery) -> {
            AMQP.BasicProperties replyProps = new AMQP.BasicProperties
                    .Builder()
                    .correlationId(delivery.getProperties().getCorrelationId())
                    .build();

            String response = "";

            try {
                String message = new String(delivery.getBody(), "UTF-8");

                System.out.println("Got message from " + name +" : " + message);
                response= String.valueOf(setOrder(message));

            } catch (Exception e) {
                System.out.println(" [.] " + e.toString());
            } finally {
                channel.basicPublish("", delivery.getProperties().getReplyTo(), replyProps, response.getBytes("UTF-8"));
                channel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
                // RabbitMq consumer worker thread notifies the RPC server owner thread
                synchronized (monitor) {
                    monitor.notify();
                }
            }
        };

        channel.basicConsume(name, false, deliverCallback, (consumerTag -> { }));
        // Wait and be prepared to consume the message from RPC client.
        while (true) {
            synchronized (monitor) {
                try {
                    monitor.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
