package com.example.codewarsplugin.services.katas;

import com.example.codewarsplugin.models.kata.KataDirectory;
import com.example.codewarsplugin.models.kata.KataInput;
import com.example.codewarsplugin.models.kata.KataRecord;
import com.example.codewarsplugin.services.files.create.FileManager;
import com.example.codewarsplugin.services.files.create.FileServiceClient;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;

public class KataSetupService implements FileServiceClient {

    private String[] tokens;



    //this is called from a browser listener - suspect side thread. for this reason we put it on a 'good thread'
    public void setup(String url, Project project, KataSetupServiceClient client) {

        ApplicationManager.getApplication().executeOnPooledThread(() -> {

            KataRecord record = null;
            KataInput input = null;
            try{
                tokens = url.split("/");
                record = KataRecordService.getKataRecord(tokens[4]);
                record.setSelectedLanguage(tokens[6]);
                input = KataInputService.getKata(record);
            } catch (Exception e) {
                client.notifyKataFileCreationFail("Kata setup failed. Reason: something went wrong with the REST calls to the codewars.com. Try singing in again!");
                return;
            }

            KataDirectory directory = FileManager.createFiles(input, record, project, client);
            if (directory != null && directory.isComplete()){
                client.loadWorkspaceTab(directory);
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
