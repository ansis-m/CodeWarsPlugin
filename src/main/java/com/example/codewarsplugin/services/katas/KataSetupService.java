package com.example.codewarsplugin.services.katas;

import com.example.codewarsplugin.models.kata.KataInput;
import com.example.codewarsplugin.models.kata.KataRecord;

import java.util.Arrays;

public class KataSetupService implements KataRecordServiceClient, KataInputServiceClient{


    private KataSetupServiceClient client;
    private String[] tokens;


    public void setup(String url, KataSetupServiceClient client) {
        this.client = client;
        tokens = url.split("/");
        KataRecordService.getKataRecord(tokens[4], this);
    }

    @Override
    public void processKataRecord(KataRecord record) {
        System.out.println("Record: " + record.toString());
        record.setSelectedLanguage(tokens[6]);
        KataInputService.getKata(record, this);
    }

    @Override
    public void processKataRecordNotFound(Exception e) {

    }

    @Override
    public void processKataInput(KataInput kataInput) {
        System.out.println("Input: " + kataInput.toString());
    }

    @Override
    public void processInputNotFound(Exception e) {

    }

}
