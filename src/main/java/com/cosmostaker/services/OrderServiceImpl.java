package com.cosmostaker.services;

import com.cosmostaker.dto.OrderCallbackDto;
import com.cosmostaker.entities.Orders;
import com.cosmostaker.exception.ResourceNotFoundException;
import com.cosmostaker.repositories.OrderRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderServiceImpl implements OrderService {


    private final RazorpayClient razorpayClient;
    private final OrderRepository orderRepository;

    @Autowired
    public OrderServiceImpl(RazorpayClient razorpayClient, OrderRepository orderRepository) {
        this.razorpayClient = razorpayClient;
        this.orderRepository = orderRepository;
    }

    @Override
    public Orders createOrder(Long userId, Long productId, Double amount) {
        try {
            JSONObject orderRequest = new JSONObject();
            orderRequest.put("amount", amount * 100);
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", "order_rcptid_" + userId);

         Order razorpayOrder = razorpayClient.orders.create(orderRequest);

            Orders order = new Orders();
            order.setUserId(userId);
            order.setProductId(productId);
            order.setAmount(amount);
            order.setRazorpayOrderId(razorpayOrder.get("id"));
            order.setOrderStatus(razorpayOrder.get("status"));
            return orderRepository.save(order);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create Razorpay order", e);
        }
    }

    @Override
    public void handlePaymentCallback(OrderCallbackDto callback) {
        Orders order = orderRepository.findByRazorpayOrderId(callback.getRazorpayOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        if (callback.getRazorpayPaymentId() != null && callback.getRazorpaySignature() != null) {
            order.setRazorpayPaymentId(callback.getRazorpayPaymentId());
            order.setRazorpaySignature(callback.getRazorpaySignature());
            order.setOrderStatus("SUCCESS");
        } else {
            order.setOrderStatus("FAILED");
        }
        orderRepository.save(order);
    }
}
