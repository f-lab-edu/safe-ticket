package com.safeticket.ticket.exception;

public class TicketsNotAvailableException extends RuntimeException {
    public TicketsNotAvailableException() {
        super("이미 예약된 좌석입니다.");
    }
}
