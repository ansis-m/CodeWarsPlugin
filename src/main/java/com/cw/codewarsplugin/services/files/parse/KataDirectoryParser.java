package com.cw.codewarsplugin.services.files.parse;

import com.cw.codewarsplugin.config.StringConstants;
import com.cw.codewarsplugin.models.kata.KataDirectory;
import com.cw.codewarsplugin.models.kata.KataInput;
import com.cw.codewarsplugin.models.kata.KataRecord;
import com.cw.codewarsplugin.services.files.create.AbstractFileService;
import com.cw.codewarsplugin.services.files.create.FileServiceFactory;
import com.cw.codewarsplugin.SidePanel;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.vfs.VirtualFile;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

public class KataDirectoryParser {

    private final Project project;
    private List<VirtualFile> sourcesRoots;
    private ArrayList<KataDirectory> directoryList;
    public KataDirectoryParser(Project project) {
        this.project = project;
    }

    public void parseSourceDirectories() {
        sourcesRoots = new ArrayList<>();
        directoryList = new ArrayList<>();
        getSourcesRoots();
        sourcesRoots.forEach(this::processSourcesRoot);
    }

    private void processSourcesRoot(VirtualFile directory) {

        if (directory == null && !directory.isDirectory()) {
            return;
        }
        for (VirtualFile child : directory.getChildren()) {
            if (child.isDirectory()) {
                if (child.getName().startsWith("codewars")){
                    KataDirectory kataDirectory = new KataDirectory();
                    kataDirectory.setDirectory(child);
                    kataDirectory.setWorkDirectory(child);
                    kataDirectory.setTestDirectory(child);
                    for (VirtualFile grandchild : child.getChildren()) {
                        if(grandchild.getName().equals("metadata")){
                            kataDirectory.setMetaDataDirectory(grandchild);
                        } else if (grandchild.getName().equals("solution")){
                            kataDirectory.setWorkDirectory(grandchild);
                        } else if (grandchild.getName().equals("test")){
                            kataDirectory.setTestDirectory(grandchild);
                        }
                    }
                    if(kataDirectory.getMetaDataDirectory() == null) {
                        continue;
                    }
                    fillDirectoryWithFiles(kataDirectory);
                    if(kataDirectory.isComplete()){
                        directoryList.add(kataDirectory);
                    }
                }
            }
        }
    }

    private void fillDirectoryWithFiles(KataDirectory kataDirectory) {

        KataRecord record = null;
        KataInput input = null;
        ObjectMapper mapper = new ObjectMapper();

        for(VirtualFile metafile : kataDirectory.getMetaDataDirectory().getChildren()){
            if (!metafile.isDirectory() && metafile.getName().toLowerCase().contains("record.json")){
                try {
                    record = mapper.readValue(metafile.contentsToByteArray(), KataRecord.class);
                    kataDirectory.setRecord(record);
                    kataDirectory.setRecordFile(metafile);
                } catch (Exception e){e.printStackTrace();}
            } else if (!metafile.isDirectory() && metafile.getName().toLowerCase().contains("input.json")){
                try {
                    input = mapper.readValue(metafile.contentsToByteArray(), KataInput.class);
                    kataDirectory.setInput(input);
                    kataDirectory.setInputFile(metafile);
                } catch (Exception e){e.printStackTrace();}
            }
        }
        if(record == null || input == null) {
            return;
        }

        try {
            AbstractFileService fileService = FileServiceFactory.createFileService(input, record, project);
            String testFileName = fileService.getTestFileName();
            String workFileName = fileService.getFileName();

            for(VirtualFile file : kataDirectory.getTestDirectory().getChildren()){
                if (!file.isDirectory() && file.getName().equals(testFileName)){
                    kataDirectory.setTestFile(file);
                }
            }
            for(VirtualFile file : kataDirectory.getWorkDirectory().getChildren()){
                if (!file.isDirectory() && file.getName().equals(workFileName)){
                    kataDirectory.setWorkFile(file);
                }
            }
        } catch (Exception e) {
            ApplicationManager.getApplication().invokeLater(() -> Messages.showMessageDialog(
                    "Project directory parsing failed with " + e.getClass().getSimpleName() + ".\n " + e.getMessage(),
                    "Ups, Something Went Wrong...",
                    IconLoader.getIcon(StringConstants.MESSAGE_ICON, SidePanel.class)
            ));
        }

    }

    public void getSourcesRoots() {

        ModuleManager moduleManager = ModuleManager.getInstance(project);
        Arrays.stream(moduleManager.getModules())
            .forEach(m ->
            {
                ModuleRootManager moduleRootManager = ModuleRootManager.getInstance(m);
                ModuleType<?> moduleType = ModuleType.get(m);
                if (moduleIsJava(moduleType)) {
                    VirtualFile[] roots = moduleRootManager.getSourceRoots(false);
                    Arrays.stream(roots).filter(root -> !root.getName().equals("resources")).forEach(sourcesRoots::add);
                } else if (Stream.of("python", "web", "ruby").anyMatch(moduleType.getName().toLowerCase()::contains)) {
                    VirtualFile[] roots = moduleRootManager.getContentRoots();
                    Collections.addAll(sourcesRoots, roots);
                }
            });
    }

    private static boolean moduleIsJava(ModuleType<?> moduleType) {
        return moduleType.getName().toLowerCase().contains("java")
                && !moduleType.getName().toLowerCase().contains("unknown")
                && !moduleType.getName().toLowerCase().contains("javascript");
    }

    public ArrayList<KataDirectory> getDirectoryList() {
        parseSourceDirectories();
        return directoryList;
    }

    public void add(KataDirectory directory) {
        directoryList.add(directory);
    }
}
