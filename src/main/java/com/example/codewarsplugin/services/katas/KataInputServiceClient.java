package com.example.codewarsplugin.services.katas;

import com.example.codewarsplugin.models.kata.KataInput;

public interface KataInputServiceClient {
    void processKataInput(KataInput kataInput);

    void processInputNotFound(Exception e);

    void startSpinner();

    void stopSpinner();
}
