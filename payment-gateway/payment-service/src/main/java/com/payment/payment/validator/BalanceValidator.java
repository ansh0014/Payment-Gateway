package com.payment.payment.validator;

import com.payment.payment.exception.InsufficientBalanceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.math.BigDecimal;

@Component
public class BalanceValidator {
    
    @Autowired
    private RestTemplate restTemplate;
    
    public boolean validateSufficientBalance(String walletId, BigDecimal amount) {
        String walletUrl = "http://localhost:8081/api/wallets/" + walletId + "/available-balance";
        BigDecimal availableBalance;
        try {
            availableBalance = restTemplate.getForObject(walletUrl, BigDecimal.class);
        } catch (Exception e) {
            throw new RuntimeException("Could not retrieve wallet balance from user-service: " + e.getMessage());
        }
        
        if (availableBalance == null || availableBalance.compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Insufficient balance. Available: " + (availableBalance != null ? availableBalance : BigDecimal.ZERO) + ", Required: " + amount);
        }
        return true;
    }
    
    public boolean validatePaymentAmount(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Payment amount must be greater than zero");
        }
        return true;
    }
    
    public boolean validateTransactionLimit(BigDecimal amount, BigDecimal dailyLimit) {
        if (amount.compareTo(dailyLimit) > 0) {
            throw new IllegalArgumentException("Amount exceeds daily transaction limit: " + dailyLimit);
        }
        return true;
    }
}
