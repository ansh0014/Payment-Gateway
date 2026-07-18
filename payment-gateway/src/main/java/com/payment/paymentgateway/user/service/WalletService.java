package com.payment.paymentgateway.user.service;

import com.payment.paymentgateway.user.model.User;
import com.payment.paymentgateway.user.model.Wallet;
import com.payment.paymentgateway.user.dto.WalletResponse;
import com.payment.paymentgateway.user.repository.WalletRepository;
import com.payment.paymentgateway.user.repository.UserRepository;
import com.payment.paymentgateway.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class WalletService {
    
    @Autowired
    private WalletRepository walletRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private EntityManager entityManager;
    
    @Transactional
    public WalletResponse createWalletForUser(String userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        
        Wallet wallet = new Wallet();
        wallet.setUser(user);
        wallet.setBalance(BigDecimal.ZERO);
        wallet.setReservedAmount(BigDecimal.ZERO);
        wallet.setCreatedAt(LocalDateTime.now());
        wallet.setLastUpdated(LocalDateTime.now());
        
        Wallet savedWallet = walletRepository.save(wallet);
        return mapToResponse(savedWallet);
    }
    
    public WalletResponse getWalletByUserId(String userId) {
        Wallet wallet = walletRepository.findByUserId(userId)
            .orElseThrow(() -> new ResourceNotFoundException("Wallet not found for user: " + userId));
        return mapToResponse(wallet);
    }
    
    public WalletResponse getWalletById(String walletId) {
        Wallet wallet = walletRepository.findById(walletId)
            .orElseThrow(() -> new ResourceNotFoundException("Wallet not found with ID: " + walletId));
        return mapToResponse(wallet);
    }
    
    @Transactional
    public WalletResponse creditWallet(String walletId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
        
        Wallet wallet = walletRepository.findById(walletId)
            .orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));
        
        walletRepository.creditBalance(walletId, amount);
        entityManager.refresh(wallet);
        
        return mapToResponse(wallet);
    }
    
    @Transactional
    public WalletResponse debitWallet(String walletId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }
        
        Wallet wallet = walletRepository.findById(walletId)
            .orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));
        
        BigDecimal availableBalance = wallet.getBalance().subtract(wallet.getReservedAmount());
        if (availableBalance.compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient balance. Available: " + availableBalance);
        }
        
        walletRepository.debitBalance(walletId, amount);
        entityManager.refresh(wallet);
        
        return mapToResponse(wallet);
    }
    
    public BigDecimal getAvailableBalance(String walletId) {
        Wallet wallet = walletRepository.findById(walletId)
            .orElseThrow(() -> new ResourceNotFoundException("Wallet not found"));
        return wallet.getBalance().subtract(wallet.getReservedAmount());
    }
    
    private WalletResponse mapToResponse(Wallet wallet) {
        BigDecimal availableBalance = wallet.getBalance().subtract(wallet.getReservedAmount());
        return new WalletResponse(
            wallet.getId(),
            wallet.getUser().getId(),
            wallet.getBalance(),
            wallet.getReservedAmount(),
            availableBalance,
            wallet.getCreatedAt(),
            wallet.getLastUpdated()
        );
    }
}