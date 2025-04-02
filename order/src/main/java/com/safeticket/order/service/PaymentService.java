package com.safeticket.order.service;

import com.safeticket.order.entity.Order;

public interface PaymentService {
    void processPayment(Order order);
}
