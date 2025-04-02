package com.safeticket.order.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDTO {
    private Long orderId;
    private Integer amount;
    private Long userId;
    private String status;
}
