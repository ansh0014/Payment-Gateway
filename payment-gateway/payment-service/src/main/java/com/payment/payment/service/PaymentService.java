package com.payment.payment.service;

import com.payment.payment.model.Payment;
import com.payment.payment.model.PaymentStatus;
import com.payment.payment.model.PaymentMethod;
import com.payment.payment.dto.PaymentRequest;
import com.payment.payment.dto.PaymentResponse;
import com.payment.payment.dto.TransactionRequest;
import com.payment.payment.dto.NotificationRequest;
import com.payment.payment.repository.PaymentRepository;
import com.payment.payment.gateway.BankGatewayClient;
import com.payment.payment.queue.PaymentQueueProducer;
import com.payment.payment.queue.PaymentQueueMessage;
import com.payment.payment.exception.ResourceNotFoundException;
import com.payment.payment.exception.PaymentProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import lombok.Data;
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
    private BankGatewayClient bankGatewayClient;
    
    @Autowired
    private PaymentQueueProducer paymentQueueProducer;
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Transactional
    public PaymentResponse initiatePayment(PaymentRequest request) {
        // 1. Verify user exists in user-service
        String userServiceUrl = "http://localhost:8081/api/users/" + request.getUserId();
        try {
            restTemplate.getForObject(userServiceUrl, Object.class);
        } catch (Exception e) {
            throw new ResourceNotFoundException("User not found with ID: " + request.getUserId());
        }
        
        Payment payment = new Payment();
        payment.setReferenceNumber(generateReferenceNumber());
        payment.setUserId(request.getUserId());
        payment.setAmount(request.getAmount());
        payment.setPaymentMethod(PaymentMethod.valueOf(request.getPaymentMethod().toUpperCase()));
        payment.setDescription(request.getDescription());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setCreatedAt(LocalDateTime.now());
        
        Payment savedPayment = paymentRepository.save(payment);
        
        // 2. Publish to Redis Queue
        PaymentQueueMessage queueMsg = new PaymentQueueMessage(
            savedPayment.getId(),
            savedPayment.getUserId(),
            savedPayment.getAmount(),
            savedPayment.getPaymentMethod().toString()
        );
        queueMsg.setReferenceNumber(savedPayment.getReferenceNumber());
        paymentQueueProducer.publishPaymentMessage(queueMsg);
        
        // 3. Dispatch pending notification via notification-service
        try {
            NotificationRequest notifRequest = new NotificationRequest(
                savedPayment.getId(),
                savedPayment.getUserId(),
                "PAYMENT_PENDING",
                "Your payment of " + savedPayment.getAmount() + " is initiated and pending. Ref: " + savedPayment.getReferenceNumber()
            );
            restTemplate.postForObject("http://localhost:8084/api/notifications/send", notifRequest, Void.class);
        } catch (Exception e) {
            System.err.println("Warning: Notification could not be sent: " + e.getMessage());
        }
        
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
            
            // 1. Debit wallet or invoke bank gateway
            if (payment.getPaymentMethod() == PaymentMethod.WALLET) {
                // Get wallet ID from user-service
                String walletUrl = "http://localhost:8081/api/wallets/user/" + payment.getUserId();
                WalletDto wallet = restTemplate.getForObject(walletUrl, WalletDto.class);
                if (wallet == null) {
                    throw new ResourceNotFoundException("Wallet not found for user: " + payment.getUserId());
                }
                
                // Debit wallet in user-service
                String debitUrl = "http://localhost:8081/api/wallets/" + wallet.getId() + "/debit?amount=" + payment.getAmount();
                restTemplate.postForObject(debitUrl, null, Object.class);
            } else if (payment.getPaymentMethod() == PaymentMethod.CARD) {
                boolean gatewaySuccess = bankGatewayClient.processCardPayment("1111222233334444", "123", payment.getAmount());
                if (!gatewaySuccess) {
                    throw new PaymentProcessingException("Card payment failed by bank gateway validation");
                }
            } else if (payment.getPaymentMethod() == PaymentMethod.BANK) {
                boolean gatewaySuccess = bankGatewayClient.processBankTransfer("123456789", "IFSC0001234", payment.getAmount());
                if (!gatewaySuccess) {
                    throw new PaymentProcessingException("Bank transfer failed by bank gateway validation");
                }
            } else if (payment.getPaymentMethod() == PaymentMethod.UPI) {
                boolean gatewaySuccess = bankGatewayClient.processUPIPayment("user@upi", payment.getAmount());
                if (!gatewaySuccess) {
                    throw new PaymentProcessingException("UPI payment failed by bank gateway validation");
                }
            }
            
            // 2. Update status and transaction info
            payment.setStatus(PaymentStatus.SUCCESS);
            payment.setProcessedAt(LocalDateTime.now());
            payment.setTransactionId(generateTransactionId());
            
            // 3. Create debit transaction record in transaction-service
            TransactionRequest txnRequest = new TransactionRequest(
                payment.getId(),
                payment.getUserId(),
                payment.getAmount(),
                "Payment processed: " + payment.getReferenceNumber(),
                "{\"method\": \"" + payment.getPaymentMethod() + "\"}"
            );
            restTemplate.postForObject("http://localhost:8083/api/transactions/debit", txnRequest, Object.class);
            
            // 4. Send success notification via notification-service
            try {
                NotificationRequest notifRequest = new NotificationRequest(
                    payment.getId(),
                    payment.getUserId(),
                    "PAYMENT_SUCCESS",
                    "Your payment of " + payment.getAmount() + " was successful. Ref: " + payment.getReferenceNumber()
                );
                restTemplate.postForObject("http://localhost:8084/api/notifications/send", notifRequest, Void.class);
            } catch (Exception e) {
                System.err.println("Warning: Success notification could not be sent: " + e.getMessage());
            }
            
        } catch (Exception ex) {
            payment.setStatus(PaymentStatus.FAILED);
            payment.setFailureReason(ex.getMessage());
            payment.setProcessedAt(LocalDateTime.now());
            paymentRepository.save(payment);
            
            // Send failure notification via notification-service
            try {
                NotificationRequest notifRequest = new NotificationRequest(
                    payment.getId(),
                    payment.getUserId(),
                    "PAYMENT_FAILED",
                    "Your payment of " + payment.getAmount() + " failed. Reason: " + ex.getMessage()
                );
                restTemplate.postForObject("http://localhost:8084/api/notifications/send", notifRequest, Void.class);
            } catch (Exception e) {
                System.err.println("Warning: Failure notification could not be sent: " + e.getMessage());
            }
            
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
        
        // Send cancel notification via notification-service
        try {
            NotificationRequest notifRequest = new NotificationRequest(
                payment.getId(),
                payment.getUserId(),
                "PAYMENT_CANCELLED",
                "Your payment of " + payment.getAmount() + " was cancelled."
            );
            restTemplate.postForObject("http://localhost:8084/api/notifications/send", notifRequest, Void.class);
        } catch (Exception e) {
            System.err.println("Warning: Cancel notification could not be sent: " + e.getMessage());
        }
        
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
    
    @Data
    public static class WalletDto {
        private String id;
        private String userId;
        private BigDecimal balance;
    }
}
