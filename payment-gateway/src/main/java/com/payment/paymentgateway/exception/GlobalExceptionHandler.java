package com.payment.paymentgateway.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(
            ResourceNotFoundException ex, 
            WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
            ex.getMessage(),
            "Resource Not Found",
            HttpStatus.NOT_FOUND.value(),
            LocalDateTime.now(),
            request.getDescription(false).replace("uri=", "")
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler(InsufficientBalanceException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientBalance(
            InsufficientBalanceException ex,
            WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
            ex.getMessage(),
            "Insufficient Balance",
            HttpStatus.BAD_REQUEST.value(),
            LocalDateTime.now(),
            request.getDescription(false).replace("uri=", "")
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(PaymentProcessingException.class)
    public ResponseEntity<ErrorResponse> handlePaymentProcessing(
            PaymentProcessingException ex,
            WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
            ex.getMessage(),
            "Payment Processing Error",
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            LocalDateTime.now(),
            request.getDescription(false).replace("uri=", "")
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
            IllegalArgumentException ex,
            WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
            ex.getMessage(),
            "Invalid Argument",
            HttpStatus.BAD_REQUEST.value(),
            LocalDateTime.now(),
            request.getDescription(false).replace("uri=", "")
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception ex,
            WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
            "An unexpected error occurred",
            ex.getClass().getSimpleName(),
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            LocalDateTime.now(),
            request.getDescription(false).replace("uri=", "")
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}