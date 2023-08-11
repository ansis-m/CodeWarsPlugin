package com.example.codewarsplugin.services.files.create;

import com.example.codewarsplugin.models.kata.KataDirectory;
import com.example.codewarsplugin.models.kata.KataInput;
import com.example.codewarsplugin.models.kata.KataRecord;
import com.example.codewarsplugin.services.files.parse.KataDirectoryParser;
import com.example.codewarsplugin.services.katas.KataSetupServiceClient;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.IOException;
import java.util.ArrayList;

public class FileManager {

    public static KataDirectory createFiles(KataInput input, KataRecord record, Project project, KataSetupServiceClient client) {

        ArrayList<KataDirectory> directories = new KataDirectoryParser(project).getDirectoryList();
        for(KataDirectory d: directories){
            if (d.getRecord().equals(record)) {
                return d;
            }
        }

        AbstractFileService service = null;
        try {
            service = FileServiceFactory.createFileService(input, record, project);
            service.getModules(input.getLanguageName());
            service.getSourcesRoot();
        } catch (Exception e) {
            client.notifyKataFileCreationFail("Kata setup failed with " + e.getClass().getSimpleName() + ".\n " + e.getMessage());
            return null;
        }
        AbstractFileService finalService = service;

        WriteCommandAction.runWriteCommandAction(project, () -> {
            try{
                finalService.createDirectory();
                System.out.println("created dir\n");
                finalService.initDirectory();
                System.out.println("init dir\n");
                finalService.createTestFile();
                System.out.println("created test files\n");
                finalService.createWorkFile();
                System.out.println("created work files\n");
                finalService.createRecordFile();
                System.out.println("created record files\n");
                finalService.createInputFile();
                System.out.println("created input files\n");
            } catch (Exception e) {
                client.notifyKataFileCreationFail("Kata setup failed with " + e.getClass().getSimpleName() + ".\n " + e.getMessage());
            }
        });
        return service.createKataDirectory();
    }


    public void deleteFiles(KataDirectory directory, Project project) {

        WriteCommandAction.runWriteCommandAction(project, () -> {
            System.out.println("\ndelete the kata\n");
            FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
            deleteRecursively(fileEditorManager, directory.getDirectory());
        });
    }

    private void deleteRecursively(FileEditorManager fileEditorManager, VirtualFile directory) {
        if (directory == null || !directory.exists()) return;
        try {
            if (directory.isDirectory()) {
                for (VirtualFile child : directory.getChildren()) {
                    deleteRecursively(fileEditorManager, child);
                }
            }
            if (fileEditorManager.isFileOpen(directory)) {
                fileEditorManager.closeFile(directory);
            }
            directory.delete(this);
        } catch (IOException ignored) {}
    }
}
