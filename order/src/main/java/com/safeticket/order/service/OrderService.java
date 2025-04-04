package com.safeticket.order.service;

import com.safeticket.order.dto.OrderDTO;

public interface OrderService {
    OrderDTO createOrder(OrderDTO orderDTO);
    void updateOrderStatus(OrderDTO orderDTO);
    void handleSuccessPayment(OrderDTO orderDTO);
    void handleFailedPayment(OrderDTO orderDTO);
}
