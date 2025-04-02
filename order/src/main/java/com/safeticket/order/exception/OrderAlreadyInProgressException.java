package com.safeticket.order.exception;

public class OrderAlreadyInProgressException extends RuntimeException {
    public OrderAlreadyInProgressException() {
        super("이미 처리중인 주문이 있습니다.");
    }
}
