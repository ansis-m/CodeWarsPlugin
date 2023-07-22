package com.example.codewarsplugin.services.files.create;

import com.example.codewarsplugin.models.kata.JsonSource;
import com.example.codewarsplugin.models.kata.KataInput;
import com.example.codewarsplugin.models.kata.KataRecord;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.IOException;

public class JavaFileService extends AbstractFileService{


    public JavaFileService(KataInput input, KataRecord record, FileServiceClient client) {
        super(input, record, client);
    }

    @Override
    public void createWorkFile() {
        createFile(true);
    }

    @Override
    public void createTestFile() {
        createFile(false);
    }

    public void createFile(boolean isWorkFile) {
        WriteCommandAction.runWriteCommandAction(project, () -> {
            if (directory != null) {
                VirtualFile file = null;
                try {
                    file = directory.createChildData(this, isWorkFile? getFileName() : getTestFileName());
                    file.refresh(false, true);
                    VirtualFile finalFile = file;
                    finalFile.setBinaryContent(getFileContent(isWorkFile));
                    OpenFileDescriptor descriptor = new OpenFileDescriptor(project, file);
                    FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
                    fileEditorManager.openTextEditor(descriptor, isWorkFile);
                    if(isWorkFile)
                        client.transitionToWorkView();
                } catch (IOException e) {
                    e.printStackTrace();
                    client.notifyFileCreationFailed(isWorkFile);
                }
            }
        });
    }

    private byte[] getFileContent(boolean isWorkFile) {
        return (getPackage(record.getSlug()) + (isWorkFile? input.getSetup() : input.getExampleFixture())).getBytes();
    }

    private String getPackage(String slug) {
        return "package " + "codewars.java." + slug.replaceAll("-", ".") + ";\n\n";
    }

    @Override
    public String getTestFileName() {
        int classIndex = input.getExampleFixture().indexOf("class", 0);
        int startIndex = classIndex + 5;
        int endIndex = input.getExampleFixture().indexOf('{');

        return input.getExampleFixture().substring(startIndex, endIndex).strip() + ".java";
    }

    @Override
    public String getFileName() {
        return getFileBaseName() + ".java";
    }

    @Override
    public String getFileBaseName(){
        int classIndex = input.getSetup().indexOf("class", 0);
        int startIndex = classIndex + 5;
        int endIndex = input.getSetup().indexOf('{');

        return input.getSetup().substring(startIndex, endIndex).strip();
    }

    @Override
    protected String getRecordFileName(){
        return getFileBaseName() + "Record.json";
    }

    @Override
    protected String getInputFileName(){
        return getFileBaseName() + "Input.json";
    }


    @Override
    public String getDirectoryName() {
        return "codewars.java." + record.getSlug().replaceAll("-", ".");
    }

    @Override
    public void createRecordFile() {
        createJsonFile(record);
    }

    @Override
    public void createInputFile() {
        createJsonFile(input);
    }

    public void createJsonFile(JsonSource source){

        String filename = source instanceof KataRecord? getRecordFileName() : getInputFileName();
        WriteCommandAction.runWriteCommandAction(project, () -> {
            if (metaData != null) {
                VirtualFile file = null;
                try {
                    file = metaData.createChildData(this, filename);
                    file.refresh(false, true);
                    VirtualFile finalFile = file;
                    finalFile.setBinaryContent(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(source).getBytes());
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }
}
