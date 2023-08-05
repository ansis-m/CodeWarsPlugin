package com.example.codewarsplugin.services.files.create;

import com.example.codewarsplugin.models.kata.KataDirectory;
import com.example.codewarsplugin.models.kata.KataInput;
import com.example.codewarsplugin.models.kata.KataRecord;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.project.Project;


public class FileManager {
    public static KataDirectory createFiles(KataInput input, KataRecord record, Project project) {


        FileService service = FileServiceFactory.createFileService(input, record, project);
        WriteCommandAction.runWriteCommandAction(project, () -> {
            if (service.getSourcesRoot()){
                service.createDirectory();
                service.createTestFile();
                service.createWorkFile();
                service.createRecordFile();
                service.createInputFile();
            }
        });

        return service.createKataDirectory();
    }
}
