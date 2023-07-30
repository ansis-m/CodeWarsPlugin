package com.example.codewarsplugin.services.katas;

import com.example.codewarsplugin.models.kata.SubmitResponse;

import java.net.http.HttpResponse;

public interface KataSubmitServiceClient {
    void notifyAttemptRunFailed(Exception e);

    void notifyAttemptSuccess(SubmitResponse submitResponse);

    void notifyBadAttemptStatusCode(HttpResponse<String> response);

    void notifyTestSuccess(SubmitResponse submitResponse);

    void notifyBadTestStatusCode(HttpResponse<String> response);
    void notifyTestRunFailed(Exception e);

}
