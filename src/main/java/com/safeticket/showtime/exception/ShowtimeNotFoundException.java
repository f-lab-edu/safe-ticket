package com.safeticket.showtime.exception;

public class ShowtimeNotFoundException extends RuntimeException {
    public ShowtimeNotFoundException(Long id) {
        super("공연 일정 정보를 찾을 수 없습니다. id 값: " + id);
    }
}
