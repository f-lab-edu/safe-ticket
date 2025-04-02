package com.safeticket.order.exception;

public class OrderCreationLockExpiredException extends RuntimeException {
    public OrderCreationLockExpiredException() {
        super("티켓 예약 시간이 만료되었습니다.");
    }
}
