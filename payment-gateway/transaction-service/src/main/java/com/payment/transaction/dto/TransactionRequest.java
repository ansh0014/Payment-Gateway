package com.payment.transaction.dto;

import java.math.BigDecimal;

public class TransactionRequest {
    private String paymentId;
    private String userId;
    private BigDecimal amount;
    private String description;
    private String metadata;

    public TransactionRequest() {}

    public TransactionRequest(String paymentId, String userId, BigDecimal amount, String description, String metadata) {
        this.paymentId = paymentId;
        this.userId = userId;
        this.amount = amount;
        this.description = description;
        this.metadata = metadata;
    }

    public String getPaymentId() { return paymentId; }
    public void setPaymentId(String paymentId) { this.paymentId = paymentId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getMetadata() { return metadata; }
    public void setMetadata(String metadata) { this.metadata = metadata; }
}
