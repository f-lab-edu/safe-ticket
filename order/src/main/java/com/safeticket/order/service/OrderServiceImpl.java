package com.safeticket.order.service;

import com.safeticket.common.util.RedisKeyUtil;
import com.safeticket.order.dto.OrderDTO;
import com.safeticket.order.dto.PaymentDTO;
import com.safeticket.order.entity.Order;
import com.safeticket.order.entity.OrderStatus;
import com.safeticket.order.entity.OrderTicket;
import com.safeticket.order.exception.OrderAlreadyInProgressException;
import com.safeticket.order.exception.OrderCreationLockExpiredException;
import com.safeticket.order.exception.OrderNotFoundException;
import com.safeticket.order.repository.OrderRepository;
import org.aspectj.weaver.ast.Or;
import org.redisson.api.RBatch;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Value("${redis.lock.ticket.reservation.lease-time-seconds}")
    private long leaseTimeSeconds;

    @Override
    @Transactional
    public OrderDTO createOrder(OrderDTO orderDTO) {
        checkDuplicateOrder(orderDTO);
        validateAndExtendReservationLock(orderDTO);

        Order order = createOrderEntity(orderDTO, OrderStatus.PENDING);
        orderRepository.save(order);

        paymentService.processPayment(order);

        return convertToOrderDTO(order);
    }

    @Override
    public void updateOrderStatus(OrderDTO orderDTO) {
        Order order = orderRepository.findById(orderDTO.getUserId())
                .orElseThrow(() -> new OrderNotFoundException(String.valueOf(orderDTO.getUserId())));
        order.updateStatus(OrderStatus.valueOf(orderDTO.getStatus()));
        orderRepository.save(order);
    }

    public void checkDuplicateOrder(OrderDTO orderDTO) {
        List<Order> existingOrderList = orderRepository.findByUserIdAndTicketIds(orderDTO.getUserId(), orderDTO.getTicketIds(), PageRequest.of(0, 1));
        if (!existingOrderList.isEmpty()) {
            Order existingOrder = existingOrderList.get(0);
            if (existingOrder.isInProgress()) {
                throw new OrderAlreadyInProgressException();
            }
        }
    }

    private Order createOrderEntity(OrderDTO orderDTO, OrderStatus orderStatus) {
        Order order = Order.builder()
                .userId(orderDTO.getUserId())
                .amount(orderDTO.getAmount())
                .status(orderStatus)
                .build();

        List<OrderTicket> orderTickets = orderDTO.getTicketIds().stream()
                .map(ticketId -> OrderTicket.builder()
                        .order(order)
                        .ticketId(ticketId)
                        .build())
                .toList();

        order.getOrderTickets().addAll(orderTickets);

        return order;
    }

    public void validateAndExtendReservationLock(OrderDTO orderDTO) {
        RBatch batch = redissonClient.createBatch();

        for (Long ticketId : orderDTO.getTicketIds()) {
            String reservationLockKey = RedisKeyUtil.getReservationLockKey(orderDTO.getUserId(), ticketId);
            String lockTicketKey = RedisKeyUtil.getLockTicketKey(String.valueOf(ticketId));

            Long reservationTtl = redisTemplate.getExpire(reservationLockKey, TimeUnit.SECONDS);
            Long lockTicketTtl = redisTemplate.getExpire(lockTicketKey, TimeUnit.SECONDS);

            if (reservationTtl <= 0 || lockTicketTtl <= 0) {
                throw new OrderCreationLockExpiredException();
            }

            batch.getBucket(reservationLockKey).expireAsync(Duration.ofSeconds(leaseTimeSeconds));
            batch.getBucket(lockTicketKey).expireAsync(Duration.ofSeconds(leaseTimeSeconds));
        }

        try {
            batch.execute();
        } catch (Exception e) {
            throw new OrderCreationLockExpiredException();
        }
    }

    private OrderDTO convertToOrderDTO(Order order) {
        return OrderDTO.builder()
                .orderId(order.getId())
                .userId(order.getUserId())
                .amount(order.getAmount())
                .ticketIds(order.getOrderTickets().stream()
                        .map(OrderTicket::getTicketId)
                        .collect(Collectors.toList()))
                .status(order.getStatus().name())
                .build();
    }
}
