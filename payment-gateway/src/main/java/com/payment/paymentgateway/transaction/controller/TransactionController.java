package com.payment.paymentgateway.transaction.controller;

import com.payment.paymentgateway.transaction.dto.TransactionResponse;
import com.payment.paymentgateway.transaction.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "*")
public class TransactionController {
    
    @Autowired
    private TransactionService transactionService;
    
    @GetMapping("/{transactionId}")
    public ResponseEntity<TransactionResponse> getTransaction(@PathVariable String transactionId) {
        TransactionResponse response = transactionService.getTransactionById(transactionId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    @GetMapping("/id/{transactionId}")
    public ResponseEntity<TransactionResponse> getByTransactionId(@PathVariable String transactionId) {
        TransactionResponse response = transactionService.getTransactionByTransactionId(transactionId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    @GetMapping("/payment/{paymentId}")
    public ResponseEntity<List<TransactionResponse>> getByPaymentId(@PathVariable String paymentId) {
        List<TransactionResponse> responses = transactionService.getTransactionsByPaymentId(paymentId);
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<TransactionResponse>> getByUserId(@PathVariable String userId) {
        List<TransactionResponse> responses = transactionService.getTransactionsByUserId(userId);
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }
    
    @GetMapping("/user/{userId}/recent")
    public ResponseEntity<List<TransactionResponse>> getRecentByUser(
            @PathVariable String userId,
            @RequestParam(defaultValue = "10") int limit) {
        List<TransactionResponse> responses = transactionService.getRecentTransactionsByUser(userId, limit);
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }
}
