package com.example.orderservice;

import org.springframework.context.ApplicationContext;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class OrderserviceApplication {

    public static void main(String[] args) throws  Exception{

        ApplicationContext applicationContext=SpringApplication.run(OrderserviceApplication.class, args);
        OrderService orderService=applicationContext.getBean(OrderService.class);
        orderService.consume("order");
    }

}
