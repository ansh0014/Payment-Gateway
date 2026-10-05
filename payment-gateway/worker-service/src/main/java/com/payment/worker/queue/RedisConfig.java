package com.payment.worker.queue;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import java.net.URI;

@Configuration
public class RedisConfig {
    
    @Value("${spring.data.redis.url:redis://localhost:6379}")
    private String redisUrl;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        try {
            URI uri = URI.create(redisUrl);
            boolean isSsl = "rediss".equalsIgnoreCase(uri.getScheme());
            String host = uri.getHost() != null ? uri.getHost() : "localhost";
            int port = uri.getPort() != -1 ? uri.getPort() : 6379;

            RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host, port);
            
            if (uri.getUserInfo() != null) {
                String userInfo = uri.getUserInfo();
                if (userInfo.contains(":")) {
                    String[] parts = userInfo.split(":", 2);
                    if (!parts[0].isEmpty() && !"default".equalsIgnoreCase(parts[0])) {
                        config.setUsername(parts[0]);
                    }
                    config.setPassword(RedisPassword.of(parts[1]));
                } else {
                    config.setPassword(RedisPassword.of(userInfo));
                }
            }

            LettuceClientConfiguration.LettuceClientConfigurationBuilder builder = LettuceClientConfiguration.builder();
            if (isSsl) {
                builder.useSsl().disablePeerVerification();
            }
            
            LettuceConnectionFactory factory = new LettuceConnectionFactory(config, builder.build());
            factory.afterPropertiesSet();
            return factory;
        } catch (Exception e) {
            LettuceConnectionFactory defaultFactory = new LettuceConnectionFactory();
            defaultFactory.afterPropertiesSet();
            return defaultFactory;
        }
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        
        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer();
        
        template.setKeySerializer(stringSerializer);
        template.setValueSerializer(jsonSerializer);
        template.setHashKeySerializer(stringSerializer);
        template.setHashValueSerializer(jsonSerializer);
        
        template.afterPropertiesSet();
        return template;
    }
}

