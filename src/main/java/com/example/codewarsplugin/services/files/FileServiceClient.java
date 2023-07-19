package com.example.codewarsplugin.services.files;

public interface FileServiceClient {
    void notifyFileExists();

    void transitionToWorkView();

    void notifyFileCreationFailed(boolean isWorkFile);
}
