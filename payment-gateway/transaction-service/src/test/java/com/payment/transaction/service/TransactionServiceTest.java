package com.payment.transaction.service;

import com.payment.transaction.dto.TransactionResponse;
import com.payment.transaction.model.Transaction;
import com.payment.transaction.model.TransactionType;
import com.payment.transaction.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionService transactionService;

    private Transaction testTransaction;

    @BeforeEach
    void setUp() {
        testTransaction = new Transaction();
        testTransaction.setId("txn-id-1");
        testTransaction.setTransactionId("TXN-12345");
        testTransaction.setPaymentId("pay-1");
        testTransaction.setUserId("user-1");
        testTransaction.setType(TransactionType.DEBIT);
        testTransaction.setAmount(new BigDecimal("100.00"));
        testTransaction.setDescription("Test debit");
        testTransaction.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void testCreateDebitTransaction_Success() {
        when(transactionRepository.save(any(Transaction.class))).thenReturn(testTransaction);

        TransactionResponse response = transactionService.createDebitTransaction(
                "pay-1", "user-1", new BigDecimal("100.00"), "Test debit", "{}"
        );

        assertNotNull(response);
        assertEquals("DEBIT", response.getType());
        assertEquals("user-1", response.getUserId());
        assertEquals(new BigDecimal("100.00"), response.getAmount());
    }

    @Test
    void testGetTransactionsByUserId() {
        when(transactionRepository.findByUserId("user-1")).thenReturn(List.of(testTransaction));

        List<TransactionResponse> responses = transactionService.getTransactionsByUserId("user-1");

        assertEquals(1, responses.size());
        assertEquals("user-1", responses.get(0).getUserId());
    }

    @Test
    void testGetTransactionById() {
        when(transactionRepository.findById("txn-id-1")).thenReturn(Optional.of(testTransaction));

        TransactionResponse response = transactionService.getTransactionById("txn-id-1");

        assertNotNull(response);
        assertEquals("txn-id-1", response.getId());
    }
}

