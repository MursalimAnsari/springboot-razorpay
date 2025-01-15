package com.cosmostaker.repositories;

import com.cosmostaker.entities.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Orders, Long> {
    Optional<Orders> findByRazorpayOrderId(String razorpayOrderId);
}