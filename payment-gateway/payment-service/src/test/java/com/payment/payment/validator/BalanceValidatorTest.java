package com.payment.payment.validator;

import com.payment.payment.exception.InsufficientBalanceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BalanceValidatorTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private BalanceValidator balanceValidator;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(balanceValidator, "userServiceUrl", "http://localhost:8081");
    }

    @Test
    void testValidateSufficientBalance_Success() {
        when(restTemplate.getForObject(eq("http://localhost:8081/api/wallets/wallet-1/available-balance"), eq(BigDecimal.class)))
                .thenReturn(new BigDecimal("500.00"));

        boolean result = balanceValidator.validateSufficientBalance("wallet-1", new BigDecimal("200.00"));

        assertTrue(result);
    }

    @Test
    void testValidateSufficientBalance_Insufficient() {
        when(restTemplate.getForObject(eq("http://localhost:8081/api/wallets/wallet-1/available-balance"), eq(BigDecimal.class)))
                .thenReturn(new BigDecimal("100.00"));

        assertThrows(InsufficientBalanceException.class, () ->
                balanceValidator.validateSufficientBalance("wallet-1", new BigDecimal("200.00"))
        );
    }

    @Test
    void testValidatePaymentAmount_Valid() {
        assertTrue(balanceValidator.validatePaymentAmount(new BigDecimal("10.00")));
    }

    @Test
    void testValidatePaymentAmount_Invalid() {
        assertThrows(IllegalArgumentException.class, () ->
                balanceValidator.validatePaymentAmount(BigDecimal.ZERO)
        );
        assertThrows(IllegalArgumentException.class, () ->
                balanceValidator.validatePaymentAmount(new BigDecimal("-5.00"))
        );
    }
}

