package com.example.codewarsplugin.services.files.create;

import com.example.codewarsplugin.models.kata.KataDirectory;
import com.example.codewarsplugin.models.kata.KataInput;
import com.example.codewarsplugin.models.kata.KataRecord;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.IOException;
import java.util.ArrayList;

public abstract class AbstractFileService implements FileService {

    final KataInput input;
    final KataRecord record;
    final Project project;
    VirtualFile baseDir;
    VirtualFile directory;
    VirtualFile sourcesRoot;
    VirtualFile metaData;
    VirtualFile testFile;
    VirtualFile workFile;
    private ArrayList<VirtualFile> sourcesRoots = new ArrayList<>();

    public AbstractFileService(KataInput input, KataRecord record, Project project){
        this.input = input;
        this.record = record;
        this.project = project;
    }

    @Override
    public void createDirectory() {


        if (sourcesRoot == null) {
            getSourcesRoot();
        }
        try {
            VirtualFile newDirectory = sourcesRoot.createChildDirectory(this, getDirectoryName());
            newDirectory.refresh(false, true);
            VirtualFile metaData = newDirectory.createChildDirectory(this, "metadata");
            metaData.refresh(false, true);
            directory = newDirectory;
            this.metaData = metaData;
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Override
    public boolean getSourcesRoot() {
        getSourcesRoots(project.getBaseDir());
        if (sourcesRoots.size() > 0) {
            this.sourcesRoot = sourcesRoots.get(0);
            return true;
        }
        return false;
    }


    @Override
    public void getSourcesRoots(VirtualFile baseDir) {
        if (baseDir != null && baseDir.isDirectory()) {
            VirtualFile[] children = baseDir.getChildren();
            for (VirtualFile child : children) {
                if (child.isDirectory()) {
                    if (isSourcesRoot(child, project)) {
                        sourcesRoots.add(child);
                    } else if (sourcesRoots.size() < 1) {
                        getSourcesRoots(child);
                    }
                }
            }
        }
    }

    public static boolean isSourcesRoot(VirtualFile directory, Project project) {
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

    @Override
    public KataDirectory createKataDirectory() {

        KataDirectory kataDirectory = new KataDirectory();
        kataDirectory.setWorkFile(workFile);
        kataDirectory.setTestFile(testFile);
        kataDirectory.setInput(input);
        kataDirectory.setRecord(record);
        kataDirectory.setDirectory(directory);
        kataDirectory.setMetaDataDirectory(metaData);

        return kataDirectory;
    }

    protected abstract String getRecordFileName();

    protected abstract String getInputFileName();
}
