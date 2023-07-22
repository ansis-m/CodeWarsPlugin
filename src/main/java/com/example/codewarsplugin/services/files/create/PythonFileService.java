package com.example.codewarsplugin.services.files.create;

import com.example.codewarsplugin.models.kata.KataInput;
import com.example.codewarsplugin.models.kata.KataRecord;

public class PythonFileService extends AbstractFileService{
    public PythonFileService(KataInput input, KataRecord record, FileServiceClient client) {
        super(input, record, client);
    }

    @Override
    public String getFileName() {
        return null;
    }

    @Override
    protected String getRecordFileName() {
        return null;
    }

    @Override
    protected String getInputFileName() {
        return null;
    }

    @Override
    public void createWorkFile() {
        throw new RuntimeException("not implemented!");
    }

    @Override
    public void createTestFile() {
        throw new RuntimeException("not implemented!");

    }

    @Override
    public String getDirectoryName() {
        return null;
    }

    @Override
    public void createRecordFile() {

    }

    @Override
    public void createInputFile() {

    }

    @Override
    public String getFileBaseName() {
        return null;
    }

    @Override
    public String getTestFileName() {
        return null;
    }
}
