package com.cosmostaker.services;

import com.cosmostaker.dto.PaymentCallbackDto;
import com.cosmostaker.entities.Orders;

public interface PaymentService {

   Orders createOrder(Long userId, Long productId, Double amount);
    void handlePaymentCallback(PaymentCallbackDto callback);
}
