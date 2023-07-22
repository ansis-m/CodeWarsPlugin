package com.example.codewarsplugin.services.files.create;

public interface FileServiceClient {
    void notifyFileExists();

    void transitionToWorkView();

    void notifyFileCreationFailed(boolean isWorkFile);
}
