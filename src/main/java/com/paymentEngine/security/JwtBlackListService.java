package com.paymentEngine.security;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;

@Service

public class JwtBlackListService {
    private final StringRedisTemplate redisTemplate;

    public JwtBlackListService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void blacklistToken(String token, Date expiration) {

        long remainingTime =
                expiration.getTime() - System.currentTimeMillis();

        if (remainingTime <= 0) {
            return;
        }

        redisTemplate.opsForValue().set(
                "blacklist:jwt:" + token,
                "revoked",
                Duration.ofMillis(remainingTime)
        );
    }

    public boolean isBlacklisted(String token) {

        return Boolean.TRUE.equals(
                redisTemplate.hasKey("blacklist:jwt:" + token)
        );
    }

}
