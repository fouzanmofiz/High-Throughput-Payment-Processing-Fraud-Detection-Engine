package com.paymentEngine.dto;

public class paymentResponse {
    private String transactionId;
    private String status;
    private String message;

    public paymentResponse(String transactionId,
                                String status,
                                String message) {
        this.transactionId = transactionId;
        this.status = status;
        this.message = message;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
