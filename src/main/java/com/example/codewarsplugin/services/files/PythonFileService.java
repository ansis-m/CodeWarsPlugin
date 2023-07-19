package com.example.codewarsplugin.services.files;

import com.example.codewarsplugin.models.kata.KataInput;
import com.example.codewarsplugin.models.kata.KataRecord;

public class PythonFileService extends AbstractFileService{
    public PythonFileService(KataInput input, KataRecord record, FileServiceClient client) {
        super(input, record, client);
    }

    @Override
    public void createWorkFile() {
        throw new RuntimeException("not implemented!");
    }

    @Override
    public void createTestFile() {
        throw new RuntimeException("not implemented!");

    }
}
