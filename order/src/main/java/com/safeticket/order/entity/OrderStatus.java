package com.safeticket.order.entity;

import lombok.Getter;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

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

    private static final Map<OrderStatus, Set<OrderStatus>> possibleTransitions = new EnumMap<>(OrderStatus.class);

    static {
        possibleTransitions.put(PENDING, EnumSet.of(PENDING, PAID));
        possibleTransitions.put(PAID, EnumSet.of(PAID, CANCELLED));
        possibleTransitions.put(CANCELLED, EnumSet.of(CANCELLED));
    }

    public boolean canTransitionTo(OrderStatus targetStatus) {
        return possibleTransitions.getOrDefault(this, EnumSet.noneOf(OrderStatus.class)).contains(targetStatus);
    }
}
