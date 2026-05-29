package com.payment.paymentgateway.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WalletResponse {
 private String id;
 private String userId;
 private BigDecimal balance;

}
