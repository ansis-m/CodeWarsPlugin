package com.example.codewarsplugin.services.files;

import com.example.codewarsplugin.models.kata.KataInput;
import com.example.codewarsplugin.models.kata.KataRecord;
import com.example.codewarsplugin.services.project.MyProjectManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;


import java.io.IOException;

public class FileService {


    private static Project project;

    public  void createFile(KataInput input, KataRecord record, FileServiceClient client){

        project = MyProjectManager.getProject();
        VirtualFile sourcesRoot = getSourcesRoot();




        String fileName = getFilename(record);
        String testFileName = getTestFilename(record);

        WriteCommandAction.runWriteCommandAction(project, () -> {

            VirtualFile directory = createDirectory(sourcesRoot, record);

            if (directory != null) {
                VirtualFile file = null;
                VirtualFile testFile = null;
                try {
                    file = directory.createChildData(this, fileName);
                    testFile = directory.createChildData(this, testFileName);
                    file.refresh(false, true);
                    testFile.refresh(false, true);
                    VirtualFile finalFile = file;
                    VirtualFile finalTestFile = testFile;

                        try {
                            finalFile.setBinaryContent((getPackage(record.getSlug()) + input.getSetup()).getBytes());
                            finalTestFile.setBinaryContent((getPackage(record.getSlug()) + input.getExampleFixture()).getBytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                            client.notifyFileCreationFailed();
                        }
                    OpenFileDescriptor descriptor = new OpenFileDescriptor(project, file);
                    OpenFileDescriptor testDescriptor = new OpenFileDescriptor(project, testFile);
                    FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
                    fileEditorManager.openTextEditor(testDescriptor, false);
                    fileEditorManager.openTextEditor(descriptor, true);
                    client.transitionToWorkView();
                } catch (IOException e) {
                    e.printStackTrace();
                    client.notifyFileCreationFailed();
                }
            }
        });
    }

    private String getPackage(String slug) {
        return "package " + slug + ";\n\n";
    }

    private String getTestFilename(KataRecord record) {
        StringBuilder base = new StringBuilder(record.getSlug());
        base.setCharAt(0, Character.toUpperCase(base.charAt(0)));
        return base + "Test" + getExtension(record);
    }

    private String getFilename(KataRecord record) {
        StringBuilder base = new StringBuilder(record.getSlug());
        base.setCharAt(0, Character.toUpperCase(base.charAt(0)));
        return base + getExtension(record);
    }

    private static String getExtension(KataRecord record) {
        return ".java";
    }


    private VirtualFile createDirectory(VirtualFile baseDir, KataRecord record) {
        if (baseDir != null) {
            try {
                VirtualFile newDirectory = baseDir.createChildDirectory(this, record.getSlug());
                newDirectory.refresh(false, true);
                return newDirectory;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private VirtualFile getSourcesRoot() {
        VirtualFile baseDir = project.getBaseDir();
        if (baseDir != null && baseDir.isDirectory()) {
            VirtualFile[] children = baseDir.getChildren();
            for (VirtualFile child : children) {
                if (child.isDirectory()) {
                    if (isSourcesRoot(child, project)) {
                        return child;
                    }
                }
            }
        }
        return null;
    }

    private boolean isSourcesRoot(VirtualFile directory, Project project) {
        Module[] modules = ModuleManager.getInstance(project).getModules();
        for (Module module : modules) {
            ModuleRootManager rootManager = ModuleRootManager.getInstance(module);
            for (VirtualFile sourceRoot : rootManager.getSourceRoots()) {
                if (VfsUtil.isAncestor(sourceRoot, directory, false)) {
                    return true;
                }
            }
        }
        return false;
    }
}
