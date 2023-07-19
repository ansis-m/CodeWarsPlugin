package com.example.codewarsplugin.services.files;

import com.example.codewarsplugin.models.kata.KataInput;
import com.example.codewarsplugin.models.kata.KataRecord;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.IOException;

import static org.apache.commons.lang.WordUtils.capitalize;

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
                    file = directory.createChildData(this, isWorkFile? getFilename() : getTestFilename());
                    file.refresh(false, true);
                    VirtualFile finalFile = file;
                    finalFile.setBinaryContent(getFileContent(isWorkFile));
                    OpenFileDescriptor descriptor = new OpenFileDescriptor(project, file);
                    FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
                    fileEditorManager.openTextEditor(descriptor, isWorkFile);
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
        return "package " + slug + ";\n\n";
    }

    private String getTestFilename() {
        //TODO fix
        return capitalize(record.getSlug()) + "Test" + ".java";
    }

    private String getFilename() {
        //TODO fix
        return capitalize(record.getSlug()) + ".java";
    }
}
