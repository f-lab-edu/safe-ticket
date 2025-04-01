package com.safeticket.order.service;

import com.safeticket.common.util.RedisKeyUtil;
import com.safeticket.order.dto.OrderDTO;
import com.safeticket.order.dto.PaymentDTO;
import com.safeticket.order.entity.Order;
import com.safeticket.order.entity.OrderStatus;
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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

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
        validateAndExtendReservationLock(orderDTO);

        Order order = createOrderEntity(orderDTO, OrderStatus.PENDING);
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

    private Order createOrderEntity(OrderDTO orderDTO, OrderStatus orderStatus) {
        return Order.builder()
                .userId(orderDTO.getUserId())
                .amount(orderDTO.getAmount())
                .ticketIds(orderDTO.getTicketIds())
                .status(orderStatus)
                .build();
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

            Instant expirationTime = Instant.ofEpochSecond(leaseTimeSeconds);
            batch.getBucket(reservationLockKey).expireAsync(expirationTime);
            batch.getBucket(lockTicketKey).expireAsync(expirationTime);
        }

        try {
            batch.execute();
        } catch (Exception e) {
            throw new OrderCreationLockExpiredException();
        }
    }
}
