package com.example.codewarsplugin.services.files.create;

import com.example.codewarsplugin.models.kata.KataDirectory;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.IOException;

public interface FileService {

    void createDirectory() throws IOException;

    void createWorkFile() throws IOException;

    void createTestFile() throws IOException;
    
    String getDirectoryName();

    void getSourcesRoot();

    void createRecordFile() throws IOException;

    void createInputFile() throws IOException;

    String getFileBaseName(String input);

    String getFileName();

    String getTestFileName();

    KataDirectory createKataDirectory();

    void getModules();
}
