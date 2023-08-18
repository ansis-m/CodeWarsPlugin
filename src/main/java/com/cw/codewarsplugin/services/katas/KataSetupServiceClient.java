package com.cw.codewarsplugin.services.katas;

import com.cw.codewarsplugin.models.kata.KataDirectory;
import com.intellij.openapi.project.Project;

public interface KataSetupServiceClient {

    Project getProject();

    void notifyKataFileCreationFail(String message);

    void loadKata(KataDirectory directory, boolean loadUrl);

    void loadWorkspace(KataDirectory directory, boolean loadUrl);
}
