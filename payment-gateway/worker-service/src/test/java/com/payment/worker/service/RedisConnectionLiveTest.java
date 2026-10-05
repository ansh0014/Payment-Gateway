package com.payment.worker.service;

import com.payment.worker.queue.RedisConfig;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RedisConnectionLiveTest {

    @Test
    void testLiveRedisConnection() {
        String redisUrl = "rediss://default:gQAAAAAAAsGtAAIgcDE5NmRhODg4NTE4MjU0YjM2YTg2MjQ2NTVjMWZmNWZlYg@above-parrot-180653.upstash.io:6379";
        RedisConfig config = new RedisConfig();
        ReflectionTestUtils.setField(config, "redisUrl", redisUrl);

        RedisConnectionFactory factory = config.redisConnectionFactory();
        RedisTemplate<String, Object> template = config.redisTemplate(factory);

        template.opsForValue().set("test:healthcheck", "OK");
        Object val = template.opsForValue().get("test:healthcheck");
        System.out.println(">>> LIVE UPSTASH REDIS RESPONSE: " + val);
        assertEquals("OK", val);
    }
}

