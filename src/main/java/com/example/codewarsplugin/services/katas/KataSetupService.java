package com.example.codewarsplugin.services.katas;

import com.example.codewarsplugin.models.kata.KataDirectory;
import com.example.codewarsplugin.models.kata.KataInput;
import com.example.codewarsplugin.models.kata.KataRecord;
import com.example.codewarsplugin.services.files.create.FileManager;
import com.example.codewarsplugin.services.files.create.FileServiceClient;

import javax.swing.*;
import java.util.Arrays;

public class KataSetupService implements FileServiceClient {


    private KataSetupServiceClient client;
    private String[] tokens;


    public void setup(String url, KataSetupServiceClient client) {


        System.out.println("setup thread: " + SwingUtilities.isEventDispatchThread());


        this.client = client;
        tokens = url.split("/");
        KataRecord record = KataRecordService.getKataRecord(tokens[4]);
        System.out.println("Record: " + record.toString());
        record.setSelectedLanguage(tokens[6]);
        KataInput input = KataInputService.getKata(record);
        System.out.println("Input: " + input.toString());
        FileManager.createFiles(input, record, client);
    }


    @Override
    public void notifyFileExists() {

    }

    @Override
    public void transitionToWorkView(KataDirectory directory) {

    }

    @Override
    public void notifyKataDirectoryCreationFailed() {

    }
}
