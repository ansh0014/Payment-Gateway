package com.payment.user.repository;

import com.payment.user.model.Wallet;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface WalletRepository extends JpaRepository<Wallet, String> {
    Optional<Wallet> findByUserId(String userId);
    
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT w FROM Wallet w WHERE w.id = :id")
    Optional<Wallet> findByIdForUpdate(@Param("id") String id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT w FROM Wallet w WHERE w.user.id = :userId")
    Optional<Wallet> findByUserIdForUpdate(@Param("userId") String userId);
    
    @Modifying
    @Transactional
    @Query("UPDATE Wallet w SET w.balance = w.balance + :amount, w.lastUpdated = CURRENT_TIMESTAMP WHERE w.id = :walletId")
    void creditBalance(@Param("walletId") String walletId, @Param("amount") BigDecimal amount);
    
    @Modifying
    @Transactional
    @Query("UPDATE Wallet w SET w.balance = w.balance - :amount, w.lastUpdated = CURRENT_TIMESTAMP WHERE w.id = :walletId AND w.balance >= :amount")
    int debitBalance(@Param("walletId") String walletId, @Param("amount") BigDecimal amount);
}
