package com.paymentEngine.dto;

public class fraudResult {
    private int riskScore;
    private String decision;
    private String reason;

    public fraudResult(int riskScore, String decision, String reason) {
        this.riskScore = riskScore;
        this.decision = decision;
        this.reason = reason;
    }

    public int getRiskScore() {
        return riskScore;
    }

    public String getDecision() {
        return decision;
    }

    public String getReason() {
        return reason;
    }

}
