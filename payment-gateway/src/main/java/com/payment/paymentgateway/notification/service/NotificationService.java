package com.payment.paymentgateway.notification.service;

import com.payment.paymentgateway.notification.model.Notification;
import com.payment.paymentgateway.notification.model.NotificationType;
import com.payment.paymentgateway.notification.repository.NotificationRepository;
import com.payment.paymentgateway.user.repository.UserRepository;
import com.payment.paymentgateway.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {
    
    @Autowired
    private NotificationRepository notificationRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Transactional
    public void sendPaymentNotification(String paymentId, String userId, NotificationType type, String message) {
        var user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setPaymentId(paymentId);
        notification.setType(type);
        notification.setMessage(message);
        notification.setEmail(user.getEmail());
        notification.setPhoneNumber(user.getPhoneNumber());
        notification.setSent(false);
        notification.setCreatedAt(LocalDateTime.now());
        
        notificationRepository.save(notification);
        
        sendEmailNotification(user.getEmail(), message);
        sendSmsNotification(user.getPhoneNumber(), message);
    }
    
    private void sendEmailNotification(String email, String message) {
        try {
            System.out.println("Sending email to: " + email + " with message: " + message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email: " + e.getMessage());
        }
    }
    
    private void sendSmsNotification(String phoneNumber, String message) {
        try {
            System.out.println("Sending SMS to: " + phoneNumber + " with message: " + message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send SMS: " + e.getMessage());
        }
    }
    
    public List<Notification> getPendingNotifications() {
        return notificationRepository.findBySent(false);
    }
    
    @Transactional
    public void markAsSent(String notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));
        notification.setSent(true);
        notification.setSentAt(LocalDateTime.now());
        notificationRepository.save(notification);
    }
}
