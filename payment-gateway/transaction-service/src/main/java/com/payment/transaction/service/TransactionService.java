package com.payment.transaction.service;

import com.payment.transaction.model.Transaction;
import com.payment.transaction.model.TransactionType;
import com.payment.transaction.dto.TransactionResponse;
import com.payment.transaction.repository.TransactionRepository;
import com.payment.transaction.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TransactionService {
    
    @Autowired
    private TransactionRepository transactionRepository;
    
    @Transactional
    public TransactionResponse createDebitTransaction(String paymentId, String userId, BigDecimal amount, String description, String metadata) {
        Transaction transaction = new Transaction();
        transaction.setTransactionId(generateTransactionId());
        transaction.setPaymentId(paymentId);
        transaction.setUserId(userId);
        transaction.setType(TransactionType.DEBIT);
        transaction.setAmount(amount);
        transaction.setDescription(description);
        transaction.setMetadata(metadata);
        transaction.setCreatedAt(LocalDateTime.now());
        
        Transaction savedTransaction = transactionRepository.save(transaction);
        return mapToResponse(savedTransaction);
    }
    
    @Transactional
    public TransactionResponse createCreditTransaction(String paymentId, String userId, BigDecimal amount, String description, String metadata) {
        Transaction transaction = new Transaction();
        transaction.setTransactionId(generateTransactionId());
        transaction.setPaymentId(paymentId);
        transaction.setUserId(userId);
        transaction.setType(TransactionType.CREDIT);
        transaction.setAmount(amount);
        transaction.setDescription(description);
        transaction.setMetadata(metadata);
        transaction.setCreatedAt(LocalDateTime.now());
        
        Transaction savedTransaction = transactionRepository.save(transaction);
        return mapToResponse(savedTransaction);
    }
    
    @Transactional
    public TransactionResponse createRefundTransaction(String paymentId, String userId, BigDecimal amount, String description, String metadata) {
        Transaction transaction = new Transaction();
        transaction.setTransactionId(generateTransactionId());
        transaction.setPaymentId(paymentId);
        transaction.setUserId(userId);
        transaction.setType(TransactionType.REFUND);
        transaction.setAmount(amount);
        transaction.setDescription(description);
        transaction.setMetadata(metadata);
        transaction.setCreatedAt(LocalDateTime.now());
        
        Transaction savedTransaction = transactionRepository.save(transaction);
        return mapToResponse(savedTransaction);
    }
    
    public TransactionResponse getTransactionById(String transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
            .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));
        return mapToResponse(transaction);
    }
    
    public TransactionResponse getTransactionByTransactionId(String transactionId) {
        Transaction transaction = transactionRepository.findByTransactionId(transactionId)
            .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));
        return mapToResponse(transaction);
    }
    
    public List<TransactionResponse> getTransactionsByPaymentId(String paymentId) {
        List<Transaction> transactions = transactionRepository.findByPaymentId(paymentId);
        return transactions.stream().map(this::mapToResponse).collect(Collectors.toList());
    }
    
    public List<TransactionResponse> getTransactionsByUserId(String userId) {
        List<Transaction> transactions = transactionRepository.findByUserId(userId);
        return transactions.stream().map(this::mapToResponse).collect(Collectors.toList());
    }
    
    public List<TransactionResponse> getRecentTransactionsByUser(String userId, int limit) {
        List<Transaction> transactions = transactionRepository.findRecentTransactionsByUser(userId);
        return transactions.stream()
            .limit(limit)
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }
    
    private String generateTransactionId() {
        return "TXN-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8);
    }
    
    private TransactionResponse mapToResponse(Transaction transaction) {
        return new TransactionResponse(
            transaction.getId(),
            transaction.getTransactionId(),
            transaction.getPaymentId(),
            transaction.getType().toString(),
            transaction.getAmount(),
            transaction.getDescription(),
            transaction.getUserId(),
            transaction.getCreatedAt(),
            transaction.getMetadata()
        );
    }
}
