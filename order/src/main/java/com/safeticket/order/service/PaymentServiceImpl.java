package com.safeticket.order.service;

import com.safeticket.order.entity.Order;
import com.safeticket.order.exception.PaymentProcessingException;
import com.safeticket.order.mapper.OrderMapper;
import com.ticket.common.dto.PaymentMessage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final RabbitTemplate rabbitTemplate;
    private final OrderMapper orderMapper = OrderMapper.INSTANCE;

    @Value("${rabbitmq.queue.payment}")
    private String paymentQueue;

    private static final Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void processPayment(Order order) {
        try {
            PaymentMessage paymentMessage = orderMapper.toPaymentMessage(order);
            paymentMessage.setStatusToPending();
            logger.info("결제 처리중... userId: {}", order.getUserId());
            rabbitTemplate.convertAndSend(paymentQueue, paymentMessage, m -> {
                m.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                return m;
            });
        } catch(Exception e) {
            logger.error("processPayment 오류 userId: {}", order.getUserId(), e);
            throw new PaymentProcessingException(String.valueOf(order.getId()));
        }
    }
}
