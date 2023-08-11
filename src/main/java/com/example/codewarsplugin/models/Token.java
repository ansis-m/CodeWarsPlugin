package com.example.codewarsplugin.models;

import java.time.LocalDateTime;

public class Token {

    private boolean success;
    private final LocalDateTime time;
    private String token;

    public Token(){
        time = LocalDateTime.now();
    }

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

    public LocalDateTime getTime() {
        return time;
    }
}
