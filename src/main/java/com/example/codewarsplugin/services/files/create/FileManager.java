package com.example.codewarsplugin.services.files.create;

import com.example.codewarsplugin.models.kata.KataDirectory;
import com.example.codewarsplugin.models.kata.KataInput;
import com.example.codewarsplugin.models.kata.KataRecord;
import com.example.codewarsplugin.services.katas.KataSetupServiceClient;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;

import java.lang.reflect.InvocationTargetException;


public class FileManager {
    public static KataDirectory createFiles(KataInput input, KataRecord record, Project project, KataSetupServiceClient client) {

        FileService service = null;
        try {
            service = FileServiceFactory.createFileService(input, record, project);
            service.getModules();
            service.getSourcesRoot();
        } catch (ClassNotFoundException | IllegalAccessException | NoSuchMethodException | InvocationTargetException |
                 InstantiationException e) {
            client.notifyKataFileCreationFail("Kata setup failed with " + e.getClass().getSimpleName() + ".\n " + e.getMessage());
            return null;
        }
        FileService finalService = service;

        WriteCommandAction.runWriteCommandAction(project, () -> {
            try{
                finalService.createDirectory();
                finalService.createTestFile();
                finalService.createWorkFile();
                finalService.createRecordFile();
                finalService.createInputFile();
            } catch (Exception e) {
                client.notifyKataFileCreationFail("Kata setup failed with " + e.getClass().getSimpleName() + ".\n " + e.getMessage());
            }

        });
        return service.createKataDirectory();
    }
}
