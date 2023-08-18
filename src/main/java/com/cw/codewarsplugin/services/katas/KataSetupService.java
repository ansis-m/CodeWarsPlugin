package com.cw.codewarsplugin.services.katas;

import com.cw.codewarsplugin.models.kata.KataDirectory;
import com.cw.codewarsplugin.models.kata.KataInput;
import com.cw.codewarsplugin.models.kata.KataRecord;
import com.cw.codewarsplugin.services.files.create.FileManager;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;

public class KataSetupService {

    private String[] tokens;


    public void setup(String url, Project project, KataSetupServiceClient client) {

        ApplicationManager.getApplication().executeOnPooledThread(() -> {

            KataRecord record = null;
            KataInput input = null;
            try{
                tokens = url.split("/");
                record = KataRecordService.getKataRecord(tokens[4]);
                record.setWorkUrl(url);
                record.setSelectedLanguage(tokens[6]);
                input = KataInputService.getKata(record);
            } catch (Exception e) {
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

}
