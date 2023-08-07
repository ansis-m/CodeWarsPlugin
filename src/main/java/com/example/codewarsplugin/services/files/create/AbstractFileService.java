package com.example.codewarsplugin.services.files.create;

import com.example.codewarsplugin.exceptions.SourcesRootNotFoundException;
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
    ArrayList<VirtualFile> sourcesRoots = new ArrayList<>();
    ArrayList<Module> modules = new ArrayList<>();

    public AbstractFileService(KataInput input, KataRecord record, Project project){
        this.input = input;
        this.record = record;
        this.project = project;
    }

    @Override
    public void createDirectory() throws IOException {

        VirtualFile newDirectory = sourcesRoot.createChildDirectory(this, getDirectoryName());
        newDirectory.refresh(false, true);
        VirtualFile metaData = newDirectory.createChildDirectory(this, "metadata");
        metaData.refresh(false, true);
        directory = newDirectory;
        this.metaData = metaData;
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
