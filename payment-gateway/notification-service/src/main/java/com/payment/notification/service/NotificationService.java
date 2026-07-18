package com.payment.notification.service;

import com.payment.notification.model.Notification;
import com.payment.notification.model.NotificationType;
import com.payment.notification.repository.NotificationRepository;
import com.payment.notification.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {
    
    @Autowired
    private NotificationRepository notificationRepository;
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Transactional
    public void sendPaymentNotification(String paymentId, String userId, NotificationType type, String message) {
        // Query user details from user-service
        String userServiceUrl = "http://localhost:8081/api/users/" + userId;
        UserDto user;
        try {
            user = restTemplate.getForObject(userServiceUrl, UserDto.class);
        } catch (Exception e) {
            throw new ResourceNotFoundException("User not found via user-service: " + e.getMessage());
        }
        
        if (user == null) {
            throw new ResourceNotFoundException("User not found with ID: " + userId);
        }
        
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

    @Data
    public static class UserDto {
        private String id;
        private String email;
        private String firstName;
        private String lastName;
        private String phoneNumber;
    }
}
