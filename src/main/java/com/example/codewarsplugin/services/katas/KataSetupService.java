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

        System.out.println("setup: " + url);
        ApplicationManager.getApplication().executeOnPooledThread(() -> {

            KataRecord record = null;
            KataInput input = null;
            try{
                tokens = url.split("/");
                record = KataRecordService.getKataRecord(tokens[4]);
                record.setWorkUrl(url);
                record.setSelectedLanguage(tokens[6]);
                System.out.println("record: " + record);
                input = KataInputService.getKata(record);
                System.out.println("input: " + input);
            } catch (Exception e) {
                System.out.println(e.getMessage() + "   \n" + e.getClass());
                e.printStackTrace();
                client.notifyKataFileCreationFail("Kata setup failed. Reason: something went wrong with the REST calls to the codewars.com. Try singing in again!");
                return;
            }

            KataDirectory directory = FileManager.createFiles(input, record, project, client);
            if (directory != null && directory.isComplete()){
                client.loadWorkspace(directory, false);
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
