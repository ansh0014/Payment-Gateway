package com.payment.transaction.repository;

import com.payment.transaction.model.Transaction;
import com.payment.transaction.model.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {
    Optional<Transaction> findByTransactionId(String transactionId);
    List<Transaction> findByPaymentId(String paymentId);
    List<Transaction> findByUserId(String userId);
    
    @Query("SELECT t FROM Transaction t WHERE t.userId = ?1 ORDER BY t.createdAt DESC")
    List<Transaction> findRecentTransactionsByUser(String userId);
    
    List<Transaction> findByType(TransactionType type);
}
