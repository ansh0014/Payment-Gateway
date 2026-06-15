package com.payment.paymentgateway.payment.repository;

import com.payment.paymentgateway.payment.model.Payment;
import com.payment.paymentgateway.payment.model.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, String> {
    Optional<Payment> findByReferenceNumber(String referenceNumber);
    
    List<Payment> findByUserId(String userId);
    
    List<Payment> findByUserIdAndStatus(String userId, PaymentStatus status);
    
    @Query("SELECT p FROM Payment p WHERE p.status = ?1 ORDER BY p.createdAt ASC")
    List<Payment> findByStatus(PaymentStatus status);
    
    @Query("SELECT p FROM Payment p WHERE p.userId = ?1 ORDER BY p.createdAt DESC")
    List<Payment> findRecentPaymentsByUser(String userId);
}