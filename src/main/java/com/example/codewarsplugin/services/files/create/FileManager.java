package com.example.codewarsplugin.services.files.create;

import com.example.codewarsplugin.models.kata.KataInput;
import com.example.codewarsplugin.models.kata.KataRecord;
import com.example.codewarsplugin.services.katas.KataSetupServiceClient;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.command.WriteCommandAction;


public class FileManager {
    public static void createFiles(KataInput input, KataRecord record, KataSetupServiceClient client) {

        var project = client.getProject();


        FileService service = FileServiceFactory.createFileService(input, record, project);

        ApplicationManager.getApplication().invokeAndWait(() -> {
            WriteCommandAction.runWriteCommandAction(project, () -> {
                service.getSourcesRoot();
                service.createDirectory();
                service.createTestFile();
                service.createWorkFile();
                service.createRecordFile();
                service.createInputFile();
            });
        }, ModalityState.defaultModalityState());
        var directory = service.createKataDirectory();
        if (directory.isComplete()) {
            //store.setCurrentKataDirectory(directory);

        } else {

        }
    }
}
