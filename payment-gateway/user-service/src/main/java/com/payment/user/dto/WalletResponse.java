package com.payment.user.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class WalletResponse {
    private String id;
    private String userId;
    private BigDecimal balance;
    private BigDecimal reservedAmount;
    private BigDecimal availableBalance;
    private LocalDateTime createdAt;
    private LocalDateTime lastUpdated;

    public WalletResponse() {}

    public WalletResponse(String id, String userId, BigDecimal balance, BigDecimal reservedAmount, BigDecimal availableBalance, LocalDateTime createdAt, LocalDateTime lastUpdated) {
        this.id = id;
        this.userId = userId;
        this.balance = balance;
        this.reservedAmount = reservedAmount;
        this.availableBalance = availableBalance;
        this.createdAt = createdAt;
        this.lastUpdated = lastUpdated;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }

    public BigDecimal getReservedAmount() { return reservedAmount; }
    public void setReservedAmount(BigDecimal reservedAmount) { this.reservedAmount = reservedAmount; }

    public BigDecimal getAvailableBalance() { return availableBalance; }
    public void setAvailableBalance(BigDecimal availableBalance) { this.availableBalance = availableBalance; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
}
