package com.cosmostaker.contollers;


import com.cosmostaker.dto.OrderCallbackDto;
import com.cosmostaker.entities.Orders;
import com.cosmostaker.services.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/orders/")
public class OrderController {
    private final OrderService paymentService;

    @Autowired
    public OrderController(OrderService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/greet")
    public String sayHello(){
        return  "hello world";
    }

    @PostMapping("/create-order")
    public ResponseEntity<Orders> createOrder(@RequestBody Map<String, Object> request) {
        Long userId = Long.valueOf(request.get("userId").toString());
        Long productId = Long.valueOf(request.get("productId").toString());
        Double amount = Double.valueOf(request.get("amount").toString());

        Orders order = paymentService.createOrder(userId, productId, amount);
        return ResponseEntity.ok(order);
    }


    @PostMapping("/callback")
    public ResponseEntity<String> handleCallback(@RequestBody OrderCallbackDto callback) {
        paymentService.handlePaymentCallback(callback);
        return ResponseEntity.ok("Payment processed successfully.");
    }
}

