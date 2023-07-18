package com.example.codewarsplugin.services.files;

import com.example.codewarsplugin.models.kata.KataInput;
import com.example.codewarsplugin.models.kata.KataRecord;
import com.example.codewarsplugin.services.project.MyProjectManager;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;

import java.io.File;
import java.io.IOException;

public class FileService {

    public static void createFile(KataInput input, KataRecord record, FileServiceClient client){

        Project project = MyProjectManager.getProject();

        System.out.println("Base path: " + project.getBasePath());
        System.out.println("Base name: " + project.getName());
        System.out.println("Base project file: " + project.getProjectFile().toString());
        System.out.println("Base project file path: " + project.getProjectFile().getPath());
        System.out.println("Base project file extension: " + project.getProjectFile().getExtension());



        String basePath = project.getBasePath();
        String filePath = basePath + "/" + record.getSlug() + getExtension(record);

        File file = new File(filePath);
        if (!file.exists()) {
                ApplicationManager.getApplication().invokeLater(() -> {
                    try {
                        file.createNewFile();
                        VirtualFile virtualFile = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(file);
                        if (virtualFile != null) {
                            OpenFileDescriptor descriptor = new OpenFileDescriptor(project, virtualFile);
                            FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
                            fileEditorManager.openFile(virtualFile, true);
                            Editor editor = fileEditorManager.openTextEditor(descriptor, true);


                            WriteCommandAction.runWriteCommandAction(project, () -> {
                                Document document = editor.getDocument();
                                document.insertString(document.getTextLength(), input.getSetup());
                                PsiDocumentManager.getInstance(project).commitDocument(document);
                            });
                            client.transitionToWorkView();
                        }
                    } catch (IOException e) {
                        client.notifyFileCreationFailed();
                    }
                });
        } else {
            client.notifyFileExists();
        }
    }

    private static String getExtension(KataRecord record) {
        return ".java";
    }
}
