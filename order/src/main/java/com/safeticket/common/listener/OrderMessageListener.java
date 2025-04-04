package com.safeticket.common.listener;

import com.safeticket.order.dto.OrderDTO;
import com.safeticket.order.mapper.OrderMapper;
import com.safeticket.order.service.OrderService;
import com.ticket.common.dto.PaymentMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderMessageListener {

    private final OrderService orderService;
    private final OrderMapper orderMapper = OrderMapper.INSTANCE;

    final Logger logger = LoggerFactory.getLogger(OrderMessageListener.class);

    @RabbitListener(queues = "${rabbitmq.queue.order}")
    public void handlePaymentMessage(PaymentMessage paymentMessage) {
        try {
            OrderDTO orderDTO = orderMapper.toOrderDTO(paymentMessage);
            logger.info("message: {}, {}", paymentMessage.getOrderId(), paymentMessage.getUserId());
            if (paymentMessage.getStatus().isSuccess()) {
                orderService.handleSuccessPayment(orderDTO);
            } else if (paymentMessage.getStatus().isFailed()) {
                orderService.handleFailedPayment(orderDTO);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    private OrderDTO convertToOrderDTO(PaymentMessage paymentMessage) {
        return OrderDTO.builder()
                .orderId(paymentMessage.getOrderId())
                .amount(paymentMessage.getAmount())
                .userId(paymentMessage.getUserId())
                .build();
    }
}
