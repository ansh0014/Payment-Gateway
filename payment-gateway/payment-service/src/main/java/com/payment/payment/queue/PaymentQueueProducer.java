package com.payment.payment.queue;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class PaymentQueueProducer {
    
    private static final String PAYMENT_QUEUE_KEY = "payment:queue";
    private static final String PROCESSING_QUEUE_KEY = "payment:processing:queue";
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    public void publishPaymentMessage(PaymentQueueMessage message) {
        try {
            String jsonMessage = objectMapper.writeValueAsString(message);
            redisTemplate.opsForList().rightPush(PAYMENT_QUEUE_KEY, jsonMessage);
        } catch (Exception e) {
            throw new RuntimeException("Failed to publish payment message: " + e.getMessage());
        }
    }
    
    public void publishProcessingMessage(PaymentQueueMessage message) {
        try {
            String jsonMessage = objectMapper.writeValueAsString(message);
            redisTemplate.opsForList().rightPush(PROCESSING_QUEUE_KEY, jsonMessage);
        } catch (Exception e) {
            throw new RuntimeException("Failed to publish processing message: " + e.getMessage());
        }
    }
    
    public long getQueueSize() {
        Long size = redisTemplate.opsForList().size(PAYMENT_QUEUE_KEY);
        return size != null ? size : 0;
    }
    
    public long getProcessingQueueSize() {
        Long size = redisTemplate.opsForList().size(PROCESSING_QUEUE_KEY);
        return size != null ? size : 0;
    }
}
