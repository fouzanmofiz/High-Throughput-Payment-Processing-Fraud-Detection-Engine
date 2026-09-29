package com.paymentEngine.controller;

import com.paymentEngine.service.redisService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/payments")
public class paymentCacheController {

    private final redisService redisService;

    public paymentCacheController(redisService redisService) {
        this.redisService = redisService;
    }

    @GetMapping("/{transactionId}/fraud-result")
    public ResponseEntity<String> getFraudResult(
            @PathVariable String transactionId) {

        String result =
                redisService.getFraudResult(transactionId);

        if (result == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(result);
    }

}
