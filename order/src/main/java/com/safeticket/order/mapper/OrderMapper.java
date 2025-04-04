package com.safeticket.order.mapper;

import com.safeticket.order.dto.OrderDTO;
import com.safeticket.order.entity.Order;
import com.ticket.common.dto.PaymentMessage;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface OrderMapper {
    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    OrderDTO toOrderDTO(PaymentMessage paymentMessage);
    PaymentMessage toPaymentMessage(OrderDTO orderDTO);

    PaymentMessage toPaymentMessage(Order order);
    Order toOrder(PaymentMessage paymentMessage);
}
