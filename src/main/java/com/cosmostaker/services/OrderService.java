package com.cosmostaker.services;

import com.cosmostaker.dto.OrderCallbackDto;
import com.cosmostaker.entities.Orders;

public interface OrderService {

   Orders createOrder(Long userId, Long productId, Double amount);
    void handlePaymentCallback(OrderCallbackDto callback);
}
