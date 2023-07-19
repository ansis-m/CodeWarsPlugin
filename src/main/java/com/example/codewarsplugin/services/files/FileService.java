package com.example.codewarsplugin.services.files;

import com.example.codewarsplugin.models.kata.KataInput;
import com.example.codewarsplugin.models.kata.KataRecord;

public interface FileService {

    void createDirectory();

    void createWorkFile();

    void createTestFile();

    void getSourcesRoot();
}
