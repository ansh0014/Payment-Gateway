package com.payment.paymentgateway.payment.controller;

import com.payment.paymentgateway.payment.dto.PaymentRequest;
import com.payment.paymentgateway.payment.dto.PaymentResponse;
import com.payment.paymentgateway.payment.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin(origins = "*")
public class PaymentController {
    
    @Autowired
    private PaymentService paymentService;
    
    @PostMapping("/initiate")
    public ResponseEntity<PaymentResponse> initiatePayment(@Valid @RequestBody PaymentRequest request) {
        PaymentResponse response = paymentService.initiatePayment(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @PostMapping("/{paymentId}/process")
    public ResponseEntity<PaymentResponse> processPayment(@PathVariable String paymentId) {
        PaymentResponse response = paymentService.processPayment(paymentId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    @GetMapping("/{paymentId}")
    public ResponseEntity<PaymentResponse> getPayment(@PathVariable String paymentId) {
        PaymentResponse response = paymentService.getPaymentById(paymentId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    @GetMapping("/reference/{referenceNumber}")
    public ResponseEntity<PaymentResponse> getPaymentByReference(@PathVariable String referenceNumber) {
        PaymentResponse response = paymentService.getPaymentByReference(referenceNumber);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PaymentResponse>> getUserPayments(@PathVariable String userId) {
        List<PaymentResponse> responses = paymentService.getPaymentsByUserId(userId);
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }
    
    @GetMapping("/pending")
    public ResponseEntity<List<PaymentResponse>> getPendingPayments() {
        List<PaymentResponse> responses = paymentService.getPendingPayments();
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }
    
    @PostMapping("/{paymentId}/cancel")
    public ResponseEntity<PaymentResponse> cancelPayment(@PathVariable String paymentId) {
        PaymentResponse response = paymentService.cancelPayment(paymentId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
