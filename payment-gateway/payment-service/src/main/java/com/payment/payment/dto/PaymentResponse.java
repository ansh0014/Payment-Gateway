package com.payment.payment.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentResponse {
    private String id;
    private String referenceNumber;
    private String userId;
    private BigDecimal amount;
    private String status;
    private String paymentMethod;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime processedAt;
    private String failureReason;
    private String transactionId;

    public PaymentResponse() {}

    public PaymentResponse(String id, String referenceNumber, String userId, BigDecimal amount, String status, String paymentMethod, String description, LocalDateTime createdAt, LocalDateTime processedAt, String failureReason, String transactionId) {
        this.id = id;
        this.referenceNumber = referenceNumber;
        this.userId = userId;
        this.amount = amount;
        this.status = status;
        this.paymentMethod = paymentMethod;
        this.description = description;
        this.createdAt = createdAt;
        this.processedAt = processedAt;
        this.failureReason = failureReason;
        this.transactionId = transactionId;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getReferenceNumber() { return referenceNumber; }
    public void setReferenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getProcessedAt() { return processedAt; }
    public void setProcessedAt(LocalDateTime processedAt) { this.processedAt = processedAt; }

    public String getFailureReason() { return failureReason; }
    public void setFailureReason(String failureReason) { this.failureReason = failureReason; }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }
}
