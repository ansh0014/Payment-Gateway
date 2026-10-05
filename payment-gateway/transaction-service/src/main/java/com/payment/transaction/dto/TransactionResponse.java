package com.payment.transaction.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionResponse {
    private String id;
    private String transactionId;
    private String paymentId;
    private String type;
    private BigDecimal amount;
    private String description;
    private String userId;
    private LocalDateTime createdAt;
    private String metadata;

    public TransactionResponse() {}

    public TransactionResponse(String id, String transactionId, String paymentId, String type, BigDecimal amount, String description, String userId, LocalDateTime createdAt, String metadata) {
        this.id = id;
        this.transactionId = transactionId;
        this.paymentId = paymentId;
        this.type = type;
        this.amount = amount;
        this.description = description;
        this.userId = userId;
        this.createdAt = createdAt;
        this.metadata = metadata;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public String getMetadata() { return metadata; }
    public void setMetadata(String metadata) { this.metadata = metadata; }
}
