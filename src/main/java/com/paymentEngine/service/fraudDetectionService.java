package com.paymentEngine.service;
import com.paymentEngine.dto.fraudResult;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
@Service
public class fraudDetectionService {
    public fraudResult analyze(
            BigDecimal amount,
            String currency) {

        int riskScore = 0;
        String reason = "Normal transaction";

        // Rule 1: High-value transaction
        if (amount.compareTo(new BigDecimal("100000")) > 0) {
            riskScore += 50;
            reason = "High transaction amount";
        }

        // Rule 2: Unexpected currency
        if (!currency.equalsIgnoreCase("INR")) {
            riskScore += 30;
            reason = "Unexpected currency";
        }

        String decision;

        if (riskScore >= 70) {
            decision = "BLOCKED";
        } else if (riskScore >= 40) {
            decision = "REVIEW";
        } else {
            decision = "APPROVED";
        }

        return new fraudResult(
                riskScore,
                decision,
                reason
        );
    }




}
