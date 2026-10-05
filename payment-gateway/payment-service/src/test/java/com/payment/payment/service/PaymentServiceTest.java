package com.payment.payment.service;

import com.payment.payment.dto.PaymentRequest;
import com.payment.payment.dto.PaymentResponse;
import com.payment.payment.exception.ResourceNotFoundException;
import com.payment.payment.gateway.BankGatewayClient;
import com.payment.payment.model.Payment;
import com.payment.payment.model.PaymentMethod;
import com.payment.payment.model.PaymentStatus;
import com.payment.payment.queue.PaymentQueueProducer;
import com.payment.payment.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private BankGatewayClient bankGatewayClient;

    @Mock
    private PaymentQueueProducer paymentQueueProducer;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private PaymentService paymentService;

    private Payment testPayment;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(paymentService, "userServiceUrl", "http://localhost:8081");
        ReflectionTestUtils.setField(paymentService, "transactionServiceUrl", "http://localhost:8083");
        ReflectionTestUtils.setField(paymentService, "notificationServiceUrl", "http://localhost:8084");

        testPayment = new Payment();
        testPayment.setId("pay-1");
        testPayment.setReferenceNumber("PAY-12345");
        testPayment.setUserId("user-1");
        testPayment.setAmount(new BigDecimal("250.00"));
        testPayment.setPaymentMethod(PaymentMethod.WALLET);
        testPayment.setStatus(PaymentStatus.PENDING);
        testPayment.setDescription("Test Payment");
        testPayment.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void testInitiatePayment_Success() {
        PaymentRequest request = new PaymentRequest();
        request.setUserId("user-1");
        request.setAmount(new BigDecimal("250.00"));
        request.setPaymentMethod("WALLET");
        request.setDescription("Test Payment");

        when(restTemplate.getForObject(eq("http://localhost:8081/api/users/user-1"), eq(Object.class)))
                .thenReturn(new Object());
        when(paymentRepository.save(any(Payment.class))).thenReturn(testPayment);

        PaymentResponse response = paymentService.initiatePayment(request);

        assertNotNull(response);
        assertEquals("pay-1", response.getId());
        assertEquals("PENDING", response.getStatus());
        verify(paymentQueueProducer, times(1)).publishPaymentMessage(any());
    }

    @Test
    void testInitiatePayment_UserNotFound() {
        PaymentRequest request = new PaymentRequest();
        request.setUserId("user-999");
        request.setAmount(new BigDecimal("250.00"));
        request.setPaymentMethod("WALLET");

        when(restTemplate.getForObject(eq("http://localhost:8081/api/users/user-999"), eq(Object.class)))
                .thenThrow(new RuntimeException("Not found"));

        assertThrows(ResourceNotFoundException.class, () -> paymentService.initiatePayment(request));
    }

    @Test
    void testGetPaymentById_Success() {
        when(paymentRepository.findById("pay-1")).thenReturn(Optional.of(testPayment));

        PaymentResponse response = paymentService.getPaymentById("pay-1");

        assertNotNull(response);
        assertEquals("pay-1", response.getId());
        assertEquals("PAY-12345", response.getReferenceNumber());
    }

    @Test
    void testCancelPayment_Success() {
        when(paymentRepository.findById("pay-1")).thenReturn(Optional.of(testPayment));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PaymentResponse response = paymentService.cancelPayment("pay-1");

        assertNotNull(response);
        assertEquals("CANCELLED", response.getStatus());
    }
}

