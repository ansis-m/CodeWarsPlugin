package com.example.codewarsplugin.services.katas;

import com.example.codewarsplugin.models.kata.SubmitResponse;

import java.net.http.HttpResponse;

public interface KataSubmitServiceClient {
    void notifyAttemptRunException(Exception e);

    void notifyAttemptSuccess(SubmitResponse submitResponse);

    void notifyBadAttemptStatusCode(HttpResponse<String> response);

    void notifyTestSuccess(SubmitResponse submitResponse);

    void notifyBadTestStatusCode(HttpResponse<String> response);
    void notifyTestRunException(Exception e);

    void notifyCommitSuccess(HttpResponse<String> response);

    void notifyCommitFailed(HttpResponse<String> response);

    void notifyCommitException(Exception e);
}
