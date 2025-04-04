package com.safeticket.order;


import com.safeticket.order.entity.Order;
import com.safeticket.order.exception.PaymentProcessingException;
import com.safeticket.order.service.PaymentServiceImpl;
import com.ticket.common.dto.PaymentMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.core.MessagePostProcessor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @Value("${rabbitmq.queue.payment}")
    private String paymentQueue;

    Order order;

    @BeforeEach
    void setUp(){
        order = Order.builder()
                .id(1L)
                .userId(1L)
                .amount(100000)
                .build();
    }

    @Test
    public void processPaymentShouldSendPaymentRequest() {
        // given
        doNothing().when(rabbitTemplate).convertAndSend(eq(paymentQueue), any(PaymentMessage.class), any(MessagePostProcessor.class));

        // when
        paymentService.processPayment(order);

        // then
        verify(rabbitTemplate).convertAndSend(eq(paymentQueue), any(PaymentMessage.class), any(MessagePostProcessor.class));
    }

    @Test
    public void processPaymentShouldThrowPaymentProcessingException() {
        // given
        doThrow(new PaymentProcessingException(String.valueOf(1L))).when(rabbitTemplate).convertAndSend(anyString(), any(PaymentMessage.class));

        // when
        PaymentProcessingException exception = assertThrows(PaymentProcessingException.class, () -> {
            paymentService.processPayment(order);
        });

        // then
        assertNotNull(exception);
        assertTrue(exception.getMessage().contains("주문번호:1"));
    }
}