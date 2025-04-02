package com.safeticket.order.dto;

import com.safeticket.order.entity.Order;
import com.safeticket.order.entity.OrderTicket;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderDTO {
    private Long orderId;
    private Long userId;
    private Integer amount;
    private List<Long> ticketIds;
    private String status;
}
