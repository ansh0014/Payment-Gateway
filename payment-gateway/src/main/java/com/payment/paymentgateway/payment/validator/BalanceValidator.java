package com.payment.paymentgateway.payment.validator;

import com.payment.paymentgateway.user.service.WalletService;
import com.payment.paymentgateway.exception.InsufficientBalanceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class BalanceValidator {
    
    @Autowired
    private WalletService walletService;
    
    public boolean validateSufficientBalance(String walletId, BigDecimal amount) {
        BigDecimal availableBalance = walletService.getAvailableBalance(walletId);
        if (availableBalance.compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Insufficient balance. Available: " + availableBalance + ", Required: " + amount);
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
