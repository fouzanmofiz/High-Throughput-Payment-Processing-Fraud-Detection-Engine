package com.paymentEngine.dto;

public class loginResponse {

    private String token;

    public loginResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}

