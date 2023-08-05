package com.example.codewarsplugin.services.katas;

import com.example.codewarsplugin.models.kata.KataDirectory;
import com.example.codewarsplugin.models.kata.KataInput;
import com.example.codewarsplugin.models.kata.KataRecord;
import com.example.codewarsplugin.services.files.create.FileManager;
import com.example.codewarsplugin.services.files.create.FileServiceClient;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;

import javax.swing.*;


public class KataSetupService implements FileServiceClient {

    private String[] tokens;



    //this is called from a browser listener - very suspect side thread. for this reason we put it on a 'good thread'
    public void setup(String url, Project project, KataSetupServiceClient client) {
        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            System.out.println("starting file creation and setup main thread = " + SwingUtilities.isEventDispatchThread());

            tokens = url.split("/");
            KataRecord record = KataRecordService.getKataRecord(tokens[4]);
            record.setSelectedLanguage(tokens[6]);
            KataInput input = KataInputService.getKata(record);

            KataDirectory directory = FileManager.createFiles(input, record, project);
            if (directory.isComplete()){
                client.loadWorkspaceTab(directory);
            } else {
                client.notifyDirectoryCreationFail(directory);
            }
        });
    }


    @Override
    public void notifyFileExists() {

    }

    @Override
    public void transitionToWorkView(KataDirectory directory) {

    }

    @Override
    public void notifyKataDirectoryCreationFailed() {

    }
}
