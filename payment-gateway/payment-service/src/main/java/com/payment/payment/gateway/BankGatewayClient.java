package com.payment.payment.gateway;

import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class BankGatewayClient {
    
    public boolean processCardPayment(String cardNumber, String cvv, BigDecimal amount) {
        return validateCardAndProcess(cardNumber, cvv, amount);
    }
    
    public boolean processBankTransfer(String accountNumber, String ifscCode, BigDecimal amount) {
        return validateBankAccountAndProcess(accountNumber, ifscCode, amount);
    }
    
    public boolean processUPIPayment(String upiId, BigDecimal amount) {
        return validateUPIAndProcess(upiId, amount);
    }
    
    private boolean validateCardAndProcess(String cardNumber, String cvv, BigDecimal amount) {
        if (cardNumber == null || cardNumber.length() < 13) {
            return false;
        }
        return true;
    }
    
    private boolean validateBankAccountAndProcess(String accountNumber, String ifscCode, BigDecimal amount) {
        if (accountNumber == null || ifscCode == null) {
            return false;
        }
        return true;
    }
    
    private boolean validateUPIAndProcess(String upiId, BigDecimal amount) {
        if (upiId == null || !upiId.contains("@")) {
            return false;
        }
        return true;
    }
}
