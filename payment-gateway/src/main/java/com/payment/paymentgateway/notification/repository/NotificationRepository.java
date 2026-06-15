package com.payment.paymentgateway.notification.repository;

import com.payment.paymentgateway.notification.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, String> {
    List<Notification> findByUserId(String userId);
    List<Notification> findByPaymentId(String paymentId);
    List<Notification> findBySent(Boolean sent);
}
