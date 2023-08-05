package com.example.codewarsplugin.services.files.create;

import com.example.codewarsplugin.models.kata.KataDirectory;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.IOException;

public interface FileService {

    void createDirectory() throws IOException;

    void createWorkFile() throws IOException;

    void createTestFile() throws IOException;
    
    String getDirectoryName();

    boolean getSourcesRoot();

    void createRecordFile() throws IOException;

    void createInputFile() throws IOException;

    String getFileBaseName(String input);

    String getFileName();

    String getTestFileName();


    void getSourcesRoots(VirtualFile baseDir);

    KataDirectory createKataDirectory();
}
