package com.example.codewarsplugin.services.katas;

import com.example.codewarsplugin.models.kata.KataRecord;

public interface KataRecordServiceClient {
    void processKataRecord(KataRecord record);

    void processKataRecordNotFound(Exception e);
}
