package com.safeticket.seat.entity;

import lombok.Getter;

@Getter
public enum SeatType {
    VIP("VIP"),
    R("R"),
    S("S"),
    A("A");

    private final String type;

    SeatType(String type) {
        this.type = type;
    }
}
