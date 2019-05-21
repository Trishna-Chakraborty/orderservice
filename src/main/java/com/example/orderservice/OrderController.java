package com.example.orderservice;

import org.springframework.web.bind.annotation.*;

@RestController
public class OrderController {
    @GetMapping("/approve")
    public OrderEntity approveOrder(){
        OrderEntity orderEntity = new OrderEntity();
        //orderEntity.setId(1);
        orderEntity.setOrderState(OrderState.APPROVED);
        return orderEntity;

    }
    @PostMapping("/order")
    public String rejectOrder(@RequestBody String message){
        System.out.println("here in orderEntity service");
        OrderEntity orderEntity = new OrderEntity();
        //orderEntity.setId(1);
        if(message.equals("true")) {
            orderEntity.setOrderState(OrderState.APPROVED);
            return String.valueOf(true);
        }
        orderEntity.setOrderState(OrderState.REJECTED);
        return  String.valueOf(false);

    }
}
