package com.example.codewarsplugin.services.katas;

import com.example.codewarsplugin.models.kata.KataDirectory;
import com.intellij.openapi.project.Project;

public interface KataSetupServiceClient {

    public Project getProject();

    void notifyKataFileCreationFail(String message);

    void loadWorkspaceTab(KataDirectory directory, boolean loadUrl);

    void loadWorkspace(KataDirectory directory, boolean loadUrl);
}
