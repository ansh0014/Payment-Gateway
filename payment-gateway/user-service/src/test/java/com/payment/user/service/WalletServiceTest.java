package com.payment.user.service;

import com.payment.user.dto.WalletResponse;
import com.payment.user.exception.InsufficientBalanceException;
import com.payment.user.exception.ResourceNotFoundException;
import com.payment.user.model.User;
import com.payment.user.model.Wallet;
import com.payment.user.repository.UserRepository;
import com.payment.user.repository.WalletRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WalletServiceTest {

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private WalletService walletService;

    private User testUser;
    private Wallet testWallet;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId("user-1");
        testUser.setEmail("user@example.com");

        testWallet = new Wallet();
        testWallet.setId("wallet-1");
        testWallet.setUser(testUser);
        testWallet.setBalance(new BigDecimal("1000.00"));
        testWallet.setReservedAmount(BigDecimal.ZERO);
        testWallet.setCreatedAt(LocalDateTime.now());
        testWallet.setLastUpdated(LocalDateTime.now());
    }

    @Test
    void testCreateWalletForUser_Success() {
        when(userRepository.findById("user-1")).thenReturn(Optional.of(testUser));
        when(walletRepository.save(any(Wallet.class))).thenReturn(testWallet);

        WalletResponse response = walletService.createWalletForUser("user-1");

        assertNotNull(response);
        assertEquals("wallet-1", response.getId());
        assertEquals("user-1", response.getUserId());
    }

    @Test
    void testCreditWallet_Success() {
        when(walletRepository.findByIdForUpdate("wallet-1")).thenReturn(Optional.of(testWallet));
        doNothing().when(walletRepository).creditBalance("wallet-1", new BigDecimal("500.00"));
        doNothing().when(entityManager).refresh(testWallet);

        WalletResponse response = walletService.creditWallet("wallet-1", new BigDecimal("500.00"));

        assertNotNull(response);
        verify(walletRepository).creditBalance("wallet-1", new BigDecimal("500.00"));
    }

    @Test
    void testDebitWallet_Success() {
        when(walletRepository.findByIdForUpdate("wallet-1")).thenReturn(Optional.of(testWallet));
        when(walletRepository.debitBalance("wallet-1", new BigDecimal("500.00"))).thenReturn(1);
        doNothing().when(entityManager).refresh(testWallet);

        WalletResponse response = walletService.debitWallet("wallet-1", new BigDecimal("500.00"));

        assertNotNull(response);
        verify(walletRepository).debitBalance("wallet-1", new BigDecimal("500.00"));
    }

    @Test
    void testDebitWallet_InsufficientBalance() {
        when(walletRepository.findByIdForUpdate("wallet-1")).thenReturn(Optional.of(testWallet));

        assertThrows(InsufficientBalanceException.class, () -> 
            walletService.debitWallet("wallet-1", new BigDecimal("1500.00"))
        );
        verify(walletRepository, never()).debitBalance(anyString(), any(BigDecimal.class));
    }

    @Test
    void testGetAvailableBalance() {
        when(walletRepository.findById("wallet-1")).thenReturn(Optional.of(testWallet));

        BigDecimal balance = walletService.getAvailableBalance("wallet-1");

        assertEquals(new BigDecimal("1000.00"), balance);
    }
}

