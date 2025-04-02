package com.safeticket.order.exception;

public class PaymentProcessingException extends RuntimeException {
    public PaymentProcessingException(String orderId) {
      super("주문번호:" + orderId + " 결제 메세지 처리 중 오류가 발생했습니다.");
    }
}
