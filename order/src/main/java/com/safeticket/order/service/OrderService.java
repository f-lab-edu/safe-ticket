package com.safeticket.order.service;

import com.safeticket.order.dto.OrderDTO;
import com.safeticket.order.entity.OrderStatus;

public interface OrderService {
    OrderDTO createOrder(OrderDTO orderDTO);
    void updateOrderStatus(OrderDTO orderDTO, OrderStatus orderStatus);
    void handleSuccessPayment(OrderDTO orderDTO);
    void handleFailedPayment(OrderDTO orderDTO);
}
