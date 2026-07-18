package com.payment.worker.queue;

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
}
