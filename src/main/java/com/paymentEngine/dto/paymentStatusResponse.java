package com.paymentEngine.dto;

public class paymentStatusResponse {

    private String transactionId;
    private String paymentStatus;
    private int riskScore;
    private boolean fraudProcessed;
    private String fraudReason;

    private String settlementId;
    private String settlementStatus;

    public paymentStatusResponse(
            String transactionId,
            String paymentStatus,
            int riskScore,
            boolean fraudProcessed,
            String fraudReason,
            String settlementId,
            String settlementStatus) {

        this.transactionId = transactionId;
        this.paymentStatus = paymentStatus;
        this.riskScore = riskScore;
        this.fraudProcessed = fraudProcessed;
        this.fraudReason = fraudReason;
        this.settlementId = settlementId;
        this.settlementStatus = settlementStatus;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public int getRiskScore() {
        return riskScore;
    }

    public boolean isFraudProcessed() {
        return fraudProcessed;
    }

    public String getFraudReason() {
        return fraudReason;
    }

    public String getSettlementId() {
        return settlementId;
    }

    public String getSettlementStatus() {
        return settlementStatus;
    }

}
