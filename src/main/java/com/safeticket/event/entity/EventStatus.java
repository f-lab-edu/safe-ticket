package com.safeticket.event.entity;

import lombok.Getter;

@Getter
public enum EventStatus {
    DRAFT("초안"),
    PUBLISHED("게시"),
    CANCELLED("취소");

    private final String status;

    EventStatus(String status) {
        this.status = status;
    }

}
