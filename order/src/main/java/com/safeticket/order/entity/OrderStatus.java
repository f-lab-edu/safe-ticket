package com.safeticket.order.entity;

import lombok.Getter;

@Getter
public enum OrderStatus {
    PENDING("PD", "대기"),
    PAID("PA", "결제완료"),
    CANCELLED("CC", "취소");

    private final String code;
    private final String description;

    OrderStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public boolean isInProgress() {
        return this == PENDING || this == PAID;
    }
}
