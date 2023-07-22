package com.example.codewarsplugin.services.files.parse;

import com.example.codewarsplugin.models.kata.KataDirectory;
import com.example.codewarsplugin.models.kata.KataInput;
import com.example.codewarsplugin.models.kata.KataRecord;
import com.example.codewarsplugin.services.files.create.FileService;
import com.example.codewarsplugin.services.files.create.FileServiceFactory;
import com.example.codewarsplugin.services.project.MyProjectManager;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;


import java.util.ArrayList;
import java.util.List;

import static com.example.codewarsplugin.services.files.create.AbstractFileService.isSourcesRoot;

public class KataDirectoryParser {

    private static Project project;
    private static List<VirtualFile> sourcesRoots;
    private static ArrayList<KataDirectory> directoryList;

    public static void parseSourceDirectories() {
        project = MyProjectManager.getProject();
        sourcesRoots = new ArrayList<>();
        directoryList = new ArrayList<>();
        getSourcesRoots().forEach(KataDirectoryParser::processSourcesRoot);
    }

    private static void processSourcesRoot(VirtualFile directory) {

        if (directory != null && directory.isDirectory()) {
            for (VirtualFile child : directory.getChildren()) {
                if (child.isDirectory()) {
                    if (child.getName().startsWith("codewars.")){
                        KataDirectory kataDirectory = new KataDirectory();
                        kataDirectory.setDirectory(child);
                        for (VirtualFile grandchild : child.getChildren()) {
                            if(grandchild.getName().equals("metadata")){
                                kataDirectory.setMetaDataDirectory(grandchild);
                            }
                        }
                        fillDirectoryWithFiles(kataDirectory);
                        if(kataDirectory.isComplete()){
                            directoryList.add(kataDirectory);
                        }
                    }
                }
            }
        }
    }

    private static void fillDirectoryWithFiles(KataDirectory kataDirectory) {

        KataRecord record = null;
        KataInput input = null;
        ObjectMapper mapper = new ObjectMapper();

        for(VirtualFile metafile : kataDirectory.getMetaDataDirectory().getChildren()){
            if (!metafile.isDirectory() && metafile.getName().contains("Record.json")){
                try {
                    record = mapper.readValue(metafile.contentsToByteArray(), KataRecord.class);
                    kataDirectory.setRecord(record);
                } catch (Exception ignored){}
            } else if (!metafile.isDirectory() && metafile.getName().contains("Input.json")){
                try {
                    input = mapper.readValue(metafile.contentsToByteArray(), KataInput.class);
                    kataDirectory.setInput(input);
                } catch (Exception ignored){}
            }
        }
        if(record == null || input == null) {
            return;
        }

        FileService fileService = FileServiceFactory.createFileService(input, record);
        String testFileName = fileService.getTestFileName();
        String workFileName = fileService.getFileName();

        for(VirtualFile file : kataDirectory.getDirectory().getChildren()){
            if (!file.isDirectory() && file.getName().equals(testFileName)){
                kataDirectory.setTestFile(file);
            } else if (!file.isDirectory() && file.getName().equals(workFileName)) {
                kataDirectory.setWorkFile(file);
            }
        }
    }

    public static List<VirtualFile> getSourcesRoots() {
        VirtualFile baseDir = project.getBaseDir();
        if (baseDir != null && baseDir.isDirectory()) {
            VirtualFile[] children = baseDir.getChildren();
            for (VirtualFile child : children) {
                if (child.isDirectory()) {
                    if (isSourcesRoot(child, project)) {
                        sourcesRoots.add(child);
                    }
                }
            }
        }
        return sourcesRoots;
    }

    public static ArrayList<KataDirectory> getDirectoryList() {
        KataDirectoryParser.parseSourceDirectories();
        return directoryList;
    }

    public static void add(KataDirectory directory) {
        directoryList.add(directory);
    }
}
