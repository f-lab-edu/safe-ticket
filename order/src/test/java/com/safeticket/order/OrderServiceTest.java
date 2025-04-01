package com.safeticket.order;

import com.safeticket.order.dto.OrderDTO;
import com.safeticket.order.entity.Order;
import com.safeticket.order.entity.OrderStatus;
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

import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private PaymentService paymentService;

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
                .ticketIds(Arrays.asList(1L, 2L))
                .status(OrderStatus.PENDING)
                .build();
    }

    @Test
    public void createOrderShouldSaveOrderAndProcessPayment() {
        // given
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        // when
        OrderDTO createdOrder = orderService.createOrder(orderDTO);

        // then
        verify(orderRepository).save(any(Order.class));
        verify(paymentService).processPayment(any(Order.class));

        assertThat(createdOrder.getUserId()).isEqualTo(order.getUserId());
        assertThat(createdOrder.getAmount()).isEqualTo(order.getAmount());
        assertThat(createdOrder.getTicketIds()).isEqualTo(order.getTicketIds());
    }

    @Test
    void updateOrderStatusShouldUpdateOrderStatus() {
        // given
        when(orderRepository.findById(orderDTO.getUserId())).thenReturn(Optional.of(order));

        // when
        orderService.updateOrderStatus(orderDTO);

        // then
        verify(orderRepository, times(1)).findById(orderDTO.getUserId());
        verify(orderRepository, times(1)).save(any(Order.class));
        assertEquals(OrderStatus.valueOf(orderDTO.getStatus()), order.getStatus());
    }

    @Test
    void updateOrderStatusShouldThrowOrderNotFoundException() {
        // given
        when(orderRepository.findById(orderDTO.getUserId())).thenReturn(Optional.empty());

        // when & then
        assertThrows(OrderNotFoundException.class, () -> orderService.updateOrderStatus(orderDTO));
    }
}