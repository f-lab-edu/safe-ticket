package com.safeticket.order;

import com.safeticket.common.advice.GlobalExceptionHandler;
import com.safeticket.order.controller.OrderController;
import com.safeticket.order.dto.OrderDTO;
import com.safeticket.order.entity.OrderStatus;
import com.safeticket.order.exception.PaymentProcessingException;
import com.safeticket.order.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    MockMvc mockMvc;

    OrderDTO orderDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(orderController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();

        orderDTO = OrderDTO.builder()
                .orderId(1L)
                .userId(1L)
                .amount(100)
                .ticketIds(List.of(1L, 2L))
                .status(OrderStatus.PENDING.getCode())
                .build();
    }

    @Test
    public void createOrderShouldReturnPendingStatus() throws Exception {
        // given
        when(orderService.createOrder(any(OrderDTO.class))).thenReturn(orderDTO);

        // when
        mockMvc.perform(post("/api/orders")
                        .contentType("application/json")
                        .content("{\"userId\": 1, \"amount\": 1000000, \"ticketIds\": [1, 2]}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId").value(1L))
                .andExpect(jsonPath("$.status").value("PD"));

        // then
        verify(orderService).createOrder(any(OrderDTO.class));
    }

    @Test
    public void createOrderShouldReturnBadRequest() throws Exception {
        // given
        when(orderService.createOrder(any(OrderDTO.class))).thenThrow(new PaymentProcessingException(String.valueOf(orderDTO.getOrderId())));

        // when
        mockMvc.perform(post("/api/orders")
                        .contentType("application/json")
                        .content("{\"userId\": 1, \"amount\": 1000000, \"ticketIds\": [1, 2]}"))
                .andExpect(status().isInternalServerError());

        // then
        verify(orderService).createOrder(any(OrderDTO.class));
    }
}