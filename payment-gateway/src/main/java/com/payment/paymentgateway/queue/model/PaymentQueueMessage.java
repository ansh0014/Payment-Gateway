package com.payment.paymentgateway.queue.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentQueueMessage implements Serializable {
    private String paymentId;
    private String referenceNumber;
    private String userId;
    private BigDecimal amount;
    private String paymentMethod;
    private LocalDateTime createdAt;
    private String status;
    
    public PaymentQueueMessage(String paymentId, String userId, BigDecimal amount, String paymentMethod) {
        this.paymentId = paymentId;
        this.userId = userId;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.createdAt = LocalDateTime.now();
        this.status = "QUEUED";
    }
}
