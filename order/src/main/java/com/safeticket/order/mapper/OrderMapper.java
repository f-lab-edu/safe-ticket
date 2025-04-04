package com.safeticket.order.mapper;

import com.safeticket.order.dto.OrderDTO;
import com.safeticket.order.entity.Order;
import com.ticket.common.dto.PaymentMessage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.factory.Mappers;

@Mapper
public interface OrderMapper {
    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    @Mappings({
            @Mapping(target = "status", ignore = true)
    })
    OrderDTO toOrderDTO(PaymentMessage paymentMessage);
    @Mappings({
            @Mapping(target = "status", ignore = true)
    })
    PaymentMessage toPaymentMessage(OrderDTO orderDTO);

    @Mappings({
            @Mapping(source = "orderId", target = "id"),
            @Mapping(target = "status", ignore = true)
    })
    Order toOrder(PaymentMessage paymentMessage);
    @Mappings({
            @Mapping(source = "id", target = "orderId"),
            @Mapping(target = "status", ignore = true)
    })
    PaymentMessage toPaymentMessage(Order order);
}
