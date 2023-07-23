package com.example.codewarsplugin.services.katas;

import com.example.codewarsplugin.models.kata.SubmitResponse;

import java.net.http.HttpResponse;

public interface KataSubmitServiceClient {
    void notifyRunFailed(Exception e);

    void notifyAttemptSuccess(SubmitResponse submitResponse);

    void notifyBadStatusCode(HttpResponse<String> response);

    void notifyTestSuccess(SubmitResponse submitResponse);
}
