package com.safeticket.order.exception;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(String orderId) {
        super("주문번호:" + orderId + " 에 해당하는 주문이 없습니다.");
    }
}