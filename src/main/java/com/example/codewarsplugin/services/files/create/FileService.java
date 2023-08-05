package com.example.codewarsplugin.services.files.create;

import com.example.codewarsplugin.models.kata.KataDirectory;
import com.intellij.openapi.vfs.VirtualFile;

public interface FileService {

    void createDirectory();

    void createWorkFile();

    void createTestFile();
    
    String getDirectoryName();

    boolean getSourcesRoot();

    void createRecordFile();

    void createInputFile();

    String getFileBaseName();

    String getFileName();

    String getTestFileName();


    void getSourcesRoots(VirtualFile baseDir);

    KataDirectory createKataDirectory();
}
