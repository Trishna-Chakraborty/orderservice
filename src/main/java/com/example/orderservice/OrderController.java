package com.example.orderservice;

import org.springframework.web.bind.annotation.GetMapping;

public class OrderController {
    @GetMapping("/approve")
    public Order approveOrder(){
        Order order= new Order();
        order.setId(1);
        order.setOrderState(OrderState.APPROVED);
        return order;

    }
    @GetMapping("/reject")
    public Order rejectOrder(){
        Order order= new Order();
        order.setId(1);
        order.setOrderState(OrderState.REJECTED);
        return order;

    }
}
