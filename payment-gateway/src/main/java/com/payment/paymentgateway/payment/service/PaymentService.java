package com.payment.paymentgateway.payment.service;

import com.payment.paymentgateway.payment.model.Payment;
import com.payment.paymentgateway.payment.model.PaymentStatus;
import com.payment.paymentgateway.payment.model.PaymentMethod;
import com.payment.paymentgateway.payment.dto.PaymentRequest;
import com.payment.paymentgateway.payment.dto.PaymentResponse;
import com.payment.paymentgateway.payment.repository.PaymentRepository;
import com.payment.paymentgateway.user.service.WalletService;
import com.payment.paymentgateway.user.repository.UserRepository;
import com.payment.paymentgateway.transaction.service.TransactionService;
import com.payment.paymentgateway.exception.ResourceNotFoundException;
import com.payment.paymentgateway.exception.PaymentProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PaymentService {
    
    @Autowired
    private PaymentRepository paymentRepository;
    
    @Autowired
    private WalletService walletService;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TransactionService transactionService;
    
    @Transactional
    public PaymentResponse initiatePayment(PaymentRequest request) {
        userRepository.findById(request.getUserId())
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        Payment payment = new Payment();
        payment.setReferenceNumber(generateReferenceNumber());
        payment.setUserId(request.getUserId());
        payment.setAmount(request.getAmount());
        payment.setPaymentMethod(PaymentMethod.valueOf(request.getPaymentMethod().toUpperCase()));
        payment.setDescription(request.getDescription());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setCreatedAt(LocalDateTime.now());
        
        Payment savedPayment = paymentRepository.save(payment);
        return mapToResponse(savedPayment);
    }
    
    @Transactional
    public PaymentResponse processPayment(String paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
            .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
        
        if (!payment.getStatus().equals(PaymentStatus.PENDING)) {
            throw new PaymentProcessingException("Payment is already " + payment.getStatus());
        }
        
        try {
            payment.setStatus(PaymentStatus.PROCESSING);
            paymentRepository.save(payment);
            
            if (payment.getPaymentMethod() == PaymentMethod.WALLET) {
                String walletId = walletService.getWalletByUserId(payment.getUserId()).getId();
                walletService.debitWallet(walletId, payment.getAmount());
            }
            
            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setProcessedAt(LocalDateTime.now());
            payment.setTransactionId(generateTransactionId());
            
            transactionService.createDebitTransaction(
                payment.getId(),
                payment.getUserId(),
                payment.getAmount(),
                "Payment processed: " + payment.getReferenceNumber(),
                "{\"method\": \"" + payment.getPaymentMethod() + "\"}"
            );
            
        } catch (Exception ex) {
            payment.setStatus(PaymentStatus.FAILED);
            payment.setFailureReason(ex.getMessage());
            payment.setProcessedAt(LocalDateTime.now());
            paymentRepository.save(payment);
            throw new PaymentProcessingException("Payment processing failed: " + ex.getMessage());
        }
        
        Payment updatedPayment = paymentRepository.save(payment);
        return mapToResponse(updatedPayment);
    }
    
    public PaymentResponse getPaymentById(String paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
            .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
        return mapToResponse(payment);
    }
    
    public PaymentResponse getPaymentByReference(String referenceNumber) {
        Payment payment = paymentRepository.findByReferenceNumber(referenceNumber)
            .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
        return mapToResponse(payment);
    }
    
    public List<PaymentResponse> getPaymentsByUserId(String userId) {
        List<Payment> payments = paymentRepository.findByUserId(userId);
        return payments.stream().map(this::mapToResponse).collect(Collectors.toList());
    }
    
    public List<PaymentResponse> getPendingPayments() {
        List<Payment> payments = paymentRepository.findByStatus(PaymentStatus.PENDING);
        return payments.stream().map(this::mapToResponse).collect(Collectors.toList());
    }
    
    @Transactional
    public PaymentResponse cancelPayment(String paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
            .orElseThrow(() -> new ResourceNotFoundException("Payment not found"));
        
        if (!payment.getStatus().equals(PaymentStatus.PENDING)) {
            throw new PaymentProcessingException("Only pending payments can be cancelled");
        }
        
        payment.setStatus(PaymentStatus.CANCELLED);
        payment.setProcessedAt(LocalDateTime.now());
        Payment updatedPayment = paymentRepository.save(payment);
        
        return mapToResponse(updatedPayment);
    }
    
    private String generateReferenceNumber() {
        return "PAY-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8);
    }
    
    private String generateTransactionId() {
        return "TXN-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 8);
    }
    
    private PaymentResponse mapToResponse(Payment payment) {
        return new PaymentResponse(
            payment.getId(),
            payment.getReferenceNumber(),
            payment.getUserId(),
            payment.getAmount(),
            payment.getStatus().toString(),
            payment.getPaymentMethod().toString(),
            payment.getDescription(),
            payment.getCreatedAt(),
            payment.getProcessedAt(),
            payment.getFailureReason(),
            payment.getTransactionId()
        );
    }
}
