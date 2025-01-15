package com.cosmostaker.config;

import com.razorpay.RazorpayClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RazorPayConfiguration {
    @Value("${razorpay.key}")
    private String KEY;

    @Value("${razorpay.secret}")
    private String SECRET;

    @Bean
    public RazorpayClient razorpayClient() {
        try {
            return new RazorpayClient(KEY, SECRET);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize Razorpay client", e);
        }
    }
}
