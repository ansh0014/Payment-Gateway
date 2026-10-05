package com.payment.worker.queue;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentQueueMessage implements Serializable {
    private String paymentId;
    private String referenceNumber;
    private String userId;
    private BigDecimal amount;
    private String paymentMethod;
    private LocalDateTime createdAt;
    private String status;

    public PaymentQueueMessage() {}

    public PaymentQueueMessage(String paymentId, String referenceNumber, String userId, BigDecimal amount, String paymentMethod, LocalDateTime createdAt, String status) {
        this.paymentId = paymentId;
        this.referenceNumber = referenceNumber;
        this.userId = userId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.createdAt = createdAt;
        this.status = status;
    }

    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }

    public String getReferenceNumber() { return referenceNumber; }
    public void setReferenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
