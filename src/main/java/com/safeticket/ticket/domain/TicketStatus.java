package com.safeticket.ticket.domain;

import lombok.Getter;

@Getter
public enum TicketStatus {
    AVAILABLE("가능"),
    RESERVED("예약"),
    CONFIRMED("확정"),
    CANCELLED("취소");

    private final String status;

    TicketStatus(String status) {
        this.status = status;
    }
}
