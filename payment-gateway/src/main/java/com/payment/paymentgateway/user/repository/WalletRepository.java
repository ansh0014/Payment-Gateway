package com.payment.paymentgateway.user.repository;

import com.payment.paymentgateway.user.model.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, String> {
    Optional<Wallet> findByUserId(String userId);
    
    @Modifying
    @Transactional
    @Query("UPDATE Wallet w SET w.balance = w.balance + :amount, w.lastUpdated = CURRENT_TIMESTAMP WHERE w.id = :walletId")
    void creditBalance(String walletId, BigDecimal amount);
    
    @Modifying
    @Transactional
    @Query("UPDATE Wallet w SET w.balance = w.balance - :amount, w.lastUpdated = CURRENT_TIMESTAMP WHERE w.id = :walletId AND w.balance >= :amount")
    int debitBalance(String walletId, BigDecimal amount);
}
