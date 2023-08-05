package com.example.codewarsplugin.services.files.create;

import com.example.codewarsplugin.models.kata.KataDirectory;
import com.example.codewarsplugin.models.kata.KataInput;
import com.example.codewarsplugin.models.kata.KataRecord;
import com.example.codewarsplugin.services.katas.KataSetupServiceClient;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;


public class FileManager {
    public static KataDirectory createFiles(KataInput input, KataRecord record, Project project, KataSetupServiceClient client) {

        FileService service = FileServiceFactory.createFileService(input, record, project);
        WriteCommandAction.runWriteCommandAction(project, () -> {

            try{
                if (!service.getSourcesRoot()){
                    client.notifyKataFileCreationFail("Kata setup failed! Cannot find a sources root directory in the current project. Create a sources root and try again!");
                    return;
                }
                service.createDirectory();
                service.createTestFile();
                service.createWorkFile();
                service.createRecordFile();
                service.createInputFile();
            } catch (Exception e) {
                client.notifyKataFileCreationFail("Kata setup failed with exception: " + e.getMessage());
            }

        });

        return service.createKataDirectory();
    }
}
