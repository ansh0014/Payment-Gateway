package com.payment.notification.service;

import com.payment.notification.exception.ResourceNotFoundException;
import com.payment.notification.model.Notification;
import com.payment.notification.model.NotificationType;
import com.payment.notification.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(notificationService, "userServiceUrl", "http://localhost:8081");
    }

    @Test
    void testSendPaymentNotification_Success() {
        NotificationService.UserDto mockUser = new NotificationService.UserDto();
        mockUser.setId("user-1");
        mockUser.setEmail("test@example.com");
        mockUser.setPhoneNumber("9876543210");

        when(restTemplate.getForObject(eq("http://localhost:8081/api/users/user-1"), eq(NotificationService.UserDto.class)))
                .thenReturn(mockUser);

        notificationService.sendPaymentNotification(
                "pay-1", "user-1", NotificationType.PAYMENT_SUCCESS, "Payment successful"
        );

        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    void testSendPaymentNotification_UserNotFound() {
        when(restTemplate.getForObject(eq("http://localhost:8081/api/users/user-999"), eq(NotificationService.UserDto.class)))
                .thenThrow(new RuntimeException("Not found"));

        assertThrows(ResourceNotFoundException.class, () ->
                notificationService.sendPaymentNotification("pay-1", "user-999", NotificationType.PAYMENT_SUCCESS, "Msg")
        );
    }

    @Test
    void testMarkAsSent_Success() {
        Notification notification = new Notification();
        notification.setId("notif-1");
        notification.setSent(false);

        when(notificationRepository.findById("notif-1")).thenReturn(Optional.of(notification));
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        notificationService.markAsSent("notif-1");

        assertTrue(notification.getSent());
        assertNotNull(notification.getSentAt());
    }
}

