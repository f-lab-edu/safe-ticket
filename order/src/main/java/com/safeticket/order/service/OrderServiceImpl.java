package com.safeticket.order.service;

import com.safeticket.order.dto.OrderDTO;
import com.safeticket.order.dto.PaymentDTO;
import com.safeticket.order.entity.Order;
import com.safeticket.order.entity.OrderStatus;
import com.safeticket.order.exception.OrderNotFoundException;
import com.safeticket.order.repository.OrderRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PaymentService paymentService;

    @Override
    @Transactional
    public OrderDTO createOrder(OrderDTO orderDTO) {
        Order order = Order.builder()
                .userId(orderDTO.getUserId())
                .amount(orderDTO.getAmount())
                .ticketIds(orderDTO.getTicketIds())
                .status(OrderStatus.PENDING)
                .build();
        orderRepository.save(order);

        paymentService.processPayment(order);

        return OrderDTO.convert(order);
    }

    @Override
    public void updateOrderStatus(OrderDTO orderDTO) {
        Order order = orderRepository.findById(orderDTO.getUserId())
                .orElseThrow(() -> new OrderNotFoundException(String.valueOf(orderDTO.getUserId())));
        order.updateStatus(OrderStatus.valueOf(orderDTO.getStatus()));
        orderRepository.save(order);
    }
}
