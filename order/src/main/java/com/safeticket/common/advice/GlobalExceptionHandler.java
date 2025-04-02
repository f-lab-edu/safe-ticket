package com.safeticket.common.advice;

import com.safeticket.event.exception.EventNotFoundException;
import com.safeticket.order.exception.OrderAlreadyInProgressException;
import com.safeticket.order.exception.OrderCreationLockExpiredException;
import com.safeticket.order.exception.OrderNotFoundException;
import com.safeticket.order.exception.PaymentProcessingException;
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

    @ExceptionHandler(PaymentProcessingException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<String> handlePaymentProcessingException(PaymentProcessingException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(OrderCreationLockExpiredException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleOrderCreationLockExpiredException(OrderCreationLockExpiredException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(OrderAlreadyInProgressException.class)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<String> handleOrderAlreadyInProgressException(OrderAlreadyInProgressException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.OK);
    }

}
