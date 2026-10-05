package com.payment.worker.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.payment.worker.queue.PaymentQueueMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PaymentWorkerTest {

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ListOperations<String, Object> listOperations;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private PaymentWorker paymentWorker;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(paymentWorker, "paymentServiceUrl", "http://localhost:8082");
    }

    @Test
    void testProcessPaymentMessage() throws Exception {
        String jsonMessage = "{\"paymentId\":\"pay-100\"}";
        PaymentQueueMessage msg = new PaymentQueueMessage();
        msg.setPaymentId("pay-100");
        msg.setUserId("user-1");
        msg.setAmount(new BigDecimal("100.00"));
        msg.setPaymentMethod("WALLET");

        when(objectMapper.readValue(jsonMessage, PaymentQueueMessage.class)).thenReturn(msg);

        ReflectionTestUtils.invokeMethod(paymentWorker, "processPaymentMessage", jsonMessage, "WORKER-1");

        verify(restTemplate, times(1)).postForObject(
                eq("http://localhost:8082/api/payments/pay-100/process"),
                isNull(),
                eq(Object.class)
        );
    }
}

