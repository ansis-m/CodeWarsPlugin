package com.example.codewarsplugin.services.files;

import com.example.codewarsplugin.models.kata.KataInput;
import com.example.codewarsplugin.models.kata.KataRecord;
import com.example.codewarsplugin.services.project.MyProjectManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.IOException;

public abstract class AbstractFileService implements FileService {

    KataInput input;
    KataRecord record;
    FileServiceClient client;
    Project project = MyProjectManager.getProject();
    VirtualFile baseDir;
    VirtualFile directory;
    VirtualFile sourcesRoot;

    public AbstractFileService(KataInput input, KataRecord record, FileServiceClient client){
        this.input = input;
        this.record = record;
        this.client = client;
    }

    @Override
    public void createDirectory() {
        WriteCommandAction.runWriteCommandAction(project, () -> {
            if (sourcesRoot != null) {
                getSourcesRoot();
            }
            try {
                VirtualFile newDirectory = sourcesRoot.createChildDirectory(this, record.getSlug());
                newDirectory.refresh(false, true);
                directory = newDirectory;
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void getSourcesRoot() {
        baseDir = project.getBaseDir();
        if (baseDir != null && baseDir.isDirectory()) {
            VirtualFile[] children = baseDir.getChildren();
            for (VirtualFile child : children) {
                if (child.isDirectory()) {
                    if (isSourcesRoot(child, project)) {
                        sourcesRoot = child;
                        return;
                    }
                }
            }
        }
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
