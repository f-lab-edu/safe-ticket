package com.safeticket.order;

import com.safeticket.order.dto.OrderDTO;
import com.safeticket.order.entity.Order;
import com.safeticket.order.entity.OrderStatus;
import com.safeticket.order.exception.OrderAlreadyInProgressException;
import com.safeticket.order.exception.OrderCreationLockExpiredException;
import com.safeticket.order.exception.OrderNotFoundException;
import com.safeticket.order.repository.OrderRepository;
import com.safeticket.order.service.OrderServiceImpl;
import com.safeticket.order.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.api.RBatch;
import org.redisson.api.RBucketAsync;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.domain.PageRequest;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private PaymentService paymentService;

    @Mock
    private RedissonClient redissonClient;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @InjectMocks
    private OrderServiceImpl orderService;

    private OrderDTO orderDTO;
    private Order order;

    @BeforeEach
    void setUp() {
        orderDTO = OrderDTO.builder()
                .userId(1L)
                .amount(100000)
                .ticketIds(Arrays.asList(1L, 2L))
                .status(OrderStatus.PENDING.name())
                .build();

        order = Order.builder()
                .userId(1L)
                .amount(100000)
                .status(OrderStatus.PENDING)
                .build();
    }

    @Test
    public void createOrderShouldSaveOrderAndProcessPayment() {
        // given
        when(orderRepository.save(any(Order.class))).thenReturn(order);
        RBatch batch = mock(RBatch.class);
        RBucketAsync<Object> bucket = mock(RBucketAsync.class);
        when(redissonClient.createBatch()).thenReturn(batch);
        when(redisTemplate.getExpire(anyString(), eq(TimeUnit.SECONDS))).thenReturn(100L);
        when(batch.getBucket(anyString())).thenReturn(bucket);

        // when
        OrderDTO createdOrder = orderService.createOrder(orderDTO);

        // then
        verify(redissonClient).createBatch();
        verify(redisTemplate, times(orderDTO.getTicketIds().size() * 2)).getExpire(anyString(), eq(TimeUnit.SECONDS));
        verify(orderRepository).save(any(Order.class));
        verify(paymentService).processPayment(any(Order.class));

        assertThat(createdOrder.getUserId()).isEqualTo(order.getUserId());
        assertThat(createdOrder.getAmount()).isEqualTo(order.getAmount());
    }

    @Test
    void updateOrderStatusShouldUpdateOrderStatus() {
        // given
        when(orderRepository.findById(orderDTO.getOrderId())).thenReturn(Optional.of(order));

        // when
        orderService.updateOrderStatus(orderDTO, OrderStatus.PAID);

        // then
        verify(orderRepository, times(1)).findById(orderDTO.getOrderId());
        verify(orderRepository, times(1)).save(any(Order.class));
        assertEquals(OrderStatus.PAID, order.getStatus());
    }

    @Test
    void checkDuplicateOrderShouldThrowExceptionWhenOrderAlreadyInProgress() {
        // given
        Order existingOrder = order;
        when(orderRepository.findByUserIdAndTicketIds(eq(orderDTO.getUserId()), eq(orderDTO.getTicketIds()), any(PageRequest.class)))
                .thenReturn(List.of(existingOrder));

        // when & then
        assertThrows(OrderAlreadyInProgressException.class, () -> {
            orderService.checkDuplicateOrder(orderDTO);
        });
    }

    @Test
    void checkDuplicateOrderShouldNotThrowException() {
        // given
        when(orderRepository.findByUserIdAndTicketIds(eq(orderDTO.getUserId()), eq(orderDTO.getTicketIds()), any(PageRequest.class)))
                .thenReturn(List.of());

        // when & then
        assertDoesNotThrow(() -> {
            orderService.checkDuplicateOrder(orderDTO);
        });
    }


    @Test
    void updateOrderStatusShouldThrowOrderNotFoundException() {
        // given
        when(orderRepository.findById(orderDTO.getOrderId())).thenReturn(Optional.empty());

        // when & then
        assertThrows(OrderNotFoundException.class, () -> orderService.updateOrderStatus(orderDTO, OrderStatus.PAID));
    }

    @Test
    void validateAndExtendReservationLockShouldExtendLock() {
        // given
        RBatch batch = mock(RBatch.class);
        RBucketAsync<Object> bucket = mock(RBucketAsync.class);
        when(redissonClient.createBatch()).thenReturn(batch);
        when(redisTemplate.getExpire(anyString(), eq(TimeUnit.SECONDS))).thenReturn(100L);
        when(batch.getBucket(anyString())).thenReturn(bucket);

        // when
        orderService.validateAndExtendReservationLock(orderDTO);

        // then
        verify(redissonClient).createBatch();
        verify(redisTemplate, times(orderDTO.getTicketIds().size() * 2)).getExpire(anyString(), eq(TimeUnit.SECONDS));
        verify(batch, times(orderDTO.getTicketIds().size() * 2)).getBucket(anyString());
        verify(bucket, times(orderDTO.getTicketIds().size() * 2)).expireAsync(any(Duration.class));
    }

    @Test
    void validateAndExtendReservationLockShouldThrowExceptionWhenLockExpired() {
        // given
        RBatch batch = mock(RBatch.class);
        when(redissonClient.createBatch()).thenReturn(batch);
        when(redisTemplate.getExpire(anyString(), eq(TimeUnit.SECONDS))).thenReturn(0L);

        // when & then
        assertThrows(OrderCreationLockExpiredException.class, () -> orderService.validateAndExtendReservationLock(orderDTO));
    }
}