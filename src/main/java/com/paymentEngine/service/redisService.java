package com.paymentEngine.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class redisService {
    private final RedisTemplate<String, String> redisTemplate;

    public redisService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void saveFraudResult(
            String transactionId,
            String decision) {

        String key = "payment:" + transactionId;

        redisTemplate.opsForValue().set(key, decision);

        System.out.println(
                "Fraud result cached in Redis: "
                        + key + " = " + decision
        );
    }

    public String getFraudResult(String transactionId) {

        String key = "payment:" + transactionId;

        return redisTemplate
                .opsForValue()
                .get(key);
    }
}
