package com.safeticket.event.exception;

public class EventNotFoundException extends RuntimeException {
    public EventNotFoundException(Long eventId) {
        super(("이벤트 ID " + eventId + "를 찾을 수 없습니다."));
    }
}
