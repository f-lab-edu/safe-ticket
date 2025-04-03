package com.safeticket.order.service;

import com.safeticket.order.entity.Order;
import com.safeticket.order.exception.PaymentProcessingException;
import com.ticket.common.dto.PaymentMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Value("${rabbitmq.queue.payment}")
    private String paymentQueue;

    private static final Logger logger = LoggerFactory.getLogger(PaymentServiceImpl.class);

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void processPayment(Order order) {
        try {
            PaymentMessage paymentMessage = convertToPaymentMessage(order);
            rabbitTemplate.convertAndSend(paymentQueue, paymentMessage, m -> {
                m.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                return m;
            });
        } catch(Exception e) {
            logger.error("processPayment 오류 userId: {}", order.getUserId(), e);
            throw new PaymentProcessingException(String.valueOf(order.getId()));
        }
    }

    private PaymentMessage convertToPaymentMessage(Order order) {
        return PaymentMessage.builder()
                .orderId(order.getId())
                .userId(order.getUserId())
                .amount(order.getAmount())
                .build();
    }
}
