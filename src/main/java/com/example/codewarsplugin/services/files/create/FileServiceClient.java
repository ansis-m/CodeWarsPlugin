package com.example.codewarsplugin.services.files.create;

import com.example.codewarsplugin.models.kata.KataDirectory;

public interface FileServiceClient {
    void notifyFileExists();

    void transitionToWorkView(KataDirectory directory);

    void notifyKataDirectoryCreationFailed();
}
