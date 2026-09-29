package com.paymentEngine.dto;

public class AiFraudResult {
    private String riskLevel;
    private String reason;

    public AiFraudResult(String riskLevel, String reason) {
        this.riskLevel = riskLevel;
        this.reason = reason;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public String getReason() {
        return reason;
    }
}
