package com.payment.paymentgateway.user.controller;

import com.payment.paymentgateway.user.dto.WalletResponse;
import com.payment.paymentgateway.user.service.WalletService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/wallets")
@CrossOrigin(origins = "*")
public class WalletController {
    
    @Autowired
    private WalletService walletService;
    
    @PostMapping("/user/{userId}")
    public ResponseEntity<WalletResponse> createWallet(@PathVariable String userId) {
        WalletResponse response = walletService.createWalletForUser(userId);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    
    @GetMapping("/{walletId}")
    public ResponseEntity<WalletResponse> getWallet(@PathVariable String walletId) {
        WalletResponse response = walletService.getWalletById(walletId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    @GetMapping("/user/{userId}")
    public ResponseEntity<WalletResponse> getWalletByUser(@PathVariable String userId) {
        WalletResponse response = walletService.getWalletByUserId(userId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    @PostMapping("/{walletId}/credit")
    public ResponseEntity<WalletResponse> creditWallet(
            @PathVariable String walletId,
            @RequestParam BigDecimal amount) {
        WalletResponse response = walletService.creditWallet(walletId, amount);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    @PostMapping("/{walletId}/debit")
    public ResponseEntity<WalletResponse> debitWallet(
            @PathVariable String walletId,
            @RequestParam BigDecimal amount) {
        WalletResponse response = walletService.debitWallet(walletId, amount);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    @GetMapping("/{walletId}/available-balance")
    public ResponseEntity<BigDecimal> getAvailableBalance(@PathVariable String walletId) {
        BigDecimal balance = walletService.getAvailableBalance(walletId);
        return new ResponseEntity<>(balance, HttpStatus.OK);
    }
}