package com.example.codewarsplugin.services.files.create;

import com.example.codewarsplugin.models.kata.KataDirectory;

public interface FileService {

    void createDirectory();

    void createWorkFile();

    void createTestFile();
    
    String getDirectoryName();

    void getSourcesRoot();

    void createRecordFile();

    void createInputFile();

    String getFileBaseName();

    String getFileName();

    String getTestFileName();

    KataDirectory createKataDirectory();
}
