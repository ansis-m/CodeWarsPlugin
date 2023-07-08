package com.example.codewarsplugin.models;

public class Token {

    private boolean success;
    private String token;

    @Override
    public String toString() {
        return "Token{" +
                "success=" + success +
                ", token='" + token + '\'' +
                '}';
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
