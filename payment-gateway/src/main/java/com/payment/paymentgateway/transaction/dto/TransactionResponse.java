package com.payment.paymentgateway.transaction.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
}
