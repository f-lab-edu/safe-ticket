package com.safeticket.common.advice;

import com.safeticket.event.exception.EventNotFoundException;
import com.safeticket.order.exception.OrderNotFoundException;
import com.safeticket.showtime.exception.ShowtimeNotFoundException;
import com.safeticket.ticket.exception.TicketsNotAvailableException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EventNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<String> handleEventNotFoundException(EventNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ShowtimeNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<String> handleShowtimeNotFoundException(ShowtimeNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(TicketsNotAvailableException.class)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> handleTicketsNotAvailableException(TicketsNotAvailableException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.OK);
    }

    @ExceptionHandler(OrderNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<String> handleOrderNotFoundException(OrderNotFoundException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.OK);
    }
}
