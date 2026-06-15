package com.payment.paymentgateway.worker.service;

import com.payment.paymentgateway.queue.model.PaymentQueueMessage;
import com.payment.paymentgateway.payment.service.PaymentService;
import com.payment.paymentgateway.transaction.service.TransactionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Async;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.TimeUnit;

@Service
public class PaymentWorker {
    
    private static final Logger logger = LoggerFactory.getLogger(PaymentWorker.class);
    private static final String PAYMENT_QUEUE_KEY = "payment:queue";
    private static final long POLLING_TIMEOUT_SECONDS = 30;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Autowired
    private PaymentService paymentService;
    
    @Autowired
    private TransactionService transactionService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    @Async
    public void worker1ProcessPayments() {
        processPayments("WORKER-1");
    }
    
    @Async
    public void worker2ProcessPayments() {
        processPayments("WORKER-2");
    }
    
    @Async
    public void worker3ProcessPayments() {
        processPayments("WORKER-3");
    }
    
    private void processPayments(String workerName) {
        logger.info("{} started processing payments", workerName);
        
        while (true) {
            try {
                Object message = redisTemplate.opsForList().leftPop(
                    PAYMENT_QUEUE_KEY, 
                    POLLING_TIMEOUT_SECONDS, 
                    TimeUnit.SECONDS
                );
                
                if (message != null) {
                    processPaymentMessage(message, workerName);
                }
            } catch (Exception e) {
                logger.error("{} encountered error: {}", workerName, e.getMessage());
            }
        }
    }
    
    private void processPaymentMessage(Object message, String workerName) {
        try {
            String jsonMessage = (String) message;
            PaymentQueueMessage paymentMsg = objectMapper.readValue(jsonMessage, PaymentQueueMessage.class);
            
            logger.info("{} processing payment: {}", workerName, paymentMsg.getPaymentId());
            
            paymentService.processPayment(paymentMsg.getPaymentId());
            
            transactionService.createDebitTransaction(
                paymentMsg.getPaymentId(),
                paymentMsg.getUserId(),
                paymentMsg.getAmount(),
                "Payment processed by " + workerName,
                "{\"worker\": \"" + workerName + "\"}"
            );
            
            logger.info("{} completed payment: {}", workerName, paymentMsg.getPaymentId());
            
        } catch (Exception e) {
            logger.error("Error processing payment message by {}: {}", workerName, e.getMessage());
            pushToDeadLetterQueue(message);
        }
    }
    
    private void pushToDeadLetterQueue(Object message) {
        try {
            String deadLetterKey = "payment:deadletter:queue";
            redisTemplate.opsForList().rightPush(deadLetterKey, message);
            redisTemplate.expire(deadLetterKey, 7, TimeUnit.DAYS);
        } catch (Exception e) {
            logger.error("Failed to push to dead-letter queue: {}", e.getMessage());
        }
    }
}
