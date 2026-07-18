package com.payment.notification.controller;

import com.payment.notification.dto.NotificationRequest;
import com.payment.notification.model.NotificationType;
import com.payment.notification.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {
    
    @Autowired
    private NotificationService notificationService;
    
    @PostMapping("/send")
    public ResponseEntity<Void> sendNotification(@RequestBody NotificationRequest request) {
        notificationService.sendPaymentNotification(
            request.getPaymentId(),
            request.getUserId(),
            NotificationType.valueOf(request.getType().toUpperCase()),
            request.getMessage()
        );
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
