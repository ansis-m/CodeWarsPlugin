package com.example.codewarsplugin.services.katas;

import com.example.codewarsplugin.models.kata.KataDirectory;
import com.intellij.openapi.project.Project;

public interface KataSetupServiceClient {

    public Project getProject();

    void notifyDirectoryCreationFail(KataDirectory directory);

    void setupWorkspace(KataDirectory directory);

    void setupDescription();
}
