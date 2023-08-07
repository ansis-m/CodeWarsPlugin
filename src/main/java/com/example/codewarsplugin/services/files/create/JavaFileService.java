package com.example.codewarsplugin.services.files.create;

import com.example.codewarsplugin.SidePanel;
import com.example.codewarsplugin.exceptions.ModuleNotFoundException;
import com.example.codewarsplugin.exceptions.SeveralModulesInProjectException;
import com.example.codewarsplugin.exceptions.SourcesRootNotFoundException;
import com.example.codewarsplugin.models.kata.JsonSource;
import com.example.codewarsplugin.models.kata.KataInput;
import com.example.codewarsplugin.models.kata.KataRecord;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.IconLoader;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.example.codewarsplugin.config.StringConstants.*;

public class JavaFileService extends AbstractFileService {

    private Pattern classPattern = Pattern.compile("(?<!\\.)\\bclass\\b");


    public JavaFileService(KataInput input, KataRecord record, Project project) {
        super(input, record, project);
    }

    @Override
    public void createWorkFile() throws IOException {
        createFile(true);
    }

    @Override
    public void createTestFile() throws IOException {
        createFile(false);
    }

    public void createFile(boolean isWorkFile) throws IOException {

        if (directory != null) {
            VirtualFile file = null;
            file = directory.createChildData(this, isWorkFile? getFileName() : getTestFileName());
            file.refresh(false, true);
            file.setBinaryContent(getFileContent(isWorkFile));

            if(isWorkFile) {
                workFile = file;
            } else {
                testFile = file;
            }
        }
    }

    private byte[] getFileContent(boolean isWorkFile) {
        return (getPackage() + (isWorkFile? input.getSetup() : input.getExampleFixture())).getBytes();
    }

    private String getPackage() {
        return "package " + getDirectoryName() + ";\n\n";
    }

    @Override
    public String getTestFileName() {
        return getFileBaseName(input.getExampleFixture()) + ".java";
    }

    @Override
    public void getModules() {
        ModuleManager moduleManager = ModuleManager.getInstance(project);
        for (Module module : moduleManager.getModules()) {
            ModuleType<?> moduleType = ModuleType.get(module);
            System.out.println("module name: " + moduleType.getName());
            if (moduleType.getName().toLowerCase().contains("java") && !moduleType.getName().toLowerCase().contains("unknown")) {
                modules.add(module);
            }
        }
        if (modules.size() < 1) {
            throw new ModuleNotFoundException("Java module not found in the current project! To setup Kata in java start a new java project or create a java module in the current project!");
        }
    }

    @Override
    public String getFileName() {
        return getFileBaseName(input.getSetup()) + ".java";
    }

    @Override
    public void getSourcesRoot() {

        Module module = null;

        if (modules.size() == 1){
            module = modules.get(0);
        } else if (modules.size() > 1) {
            AtomicInteger index = new AtomicInteger(0);
            ApplicationManager.getApplication().invokeAndWait(() -> {
                index.set(Messages.showIdeaMessageDialog(project, SEVERAL_JAVA_MODULES, PICK_JAVA_MODULE, modules.stream().map(Module::getName).toArray(String[]::new), 0, IconLoader.getIcon("/icons/new_cw_logo.svg", SidePanel.class), null));
            });
            module = modules.get(index.get());
        }


        ModuleRootManager moduleRootManager = ModuleRootManager.getInstance(module);
        VirtualFile[] roots = moduleRootManager.getSourceRoots(false);
        Arrays.stream(roots).filter(root -> !root.getName().equals("resources")).forEach(sourcesRoots::add);

        if (sourcesRoots.size() == 1) {
            this.sourcesRoot = sourcesRoots.get(0);
        }
        if (sourcesRoots.size() > 1) {
            AtomicInteger index = new AtomicInteger(0);
            ApplicationManager.getApplication().invokeAndWait(() -> {
                index.set(Messages.showIdeaMessageDialog(project, SEVERAL_JAVA_SOURCES, PICK_JAVA_SOURCE, sourcesRoots.stream().map(VirtualFile::getName).toArray(String[]::new), 0, IconLoader.getIcon("/icons/new_cw_logo.svg", SidePanel.class), null));
            });
            this.sourcesRoot = sourcesRoots.get(index.get());
        } else {
            throw new SourcesRootNotFoundException("Sources root directory not found in the current java module. Create sources root and try again!");
        }

    }

    @Override
    public String getFileBaseName(String input){

        Matcher matcher = classPattern.matcher(input);

        if (matcher.find()) {
            int classIndex = matcher.start();
            int startIndex = classIndex + 5;
            int endIndex = input.indexOf('{');
            return endIndex > startIndex? input.substring(startIndex, endIndex).strip().split("[,\\s<]")[0] : "filename";
        } else {
            return "filename";
        }
    }

    @Override
    protected String getRecordFileName(){
        return getFileBaseName(input.getSetup()) + "Record.json";
    }

    @Override
    protected String getInputFileName(){
        return getFileBaseName(input.getSetup()) + "Input.json";
    }


    @Override
    public String getDirectoryName() {
        return "codewars.java." +
                Arrays.stream(record.getSlug().split("-"))
                        .map(JavaFileService::sanitizeForPackageName)
                        .collect(Collectors.joining("."));
    }

    @Override
    public void createRecordFile() throws IOException {
        createJsonFile(record);
    }

    @Override
    public void createInputFile() throws IOException {
        createJsonFile(input);
    }

    public void createJsonFile(JsonSource source) throws IOException {

        String filename = source instanceof KataRecord? getRecordFileName() : getInputFileName();

        if (metaData != null) {
            VirtualFile file = null;
            file = metaData.createChildData(this, filename);
            file.refresh(false, true);
            VirtualFile finalFile = file;
            finalFile.setBinaryContent(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(source).getBytes(StandardCharsets.UTF_8));
        }

    }

    public static String sanitizeForPackageName(String input) {
        if (input == null || input.isEmpty()) {
            return "defaultpackage";
        }

        input = input.trim().replaceAll("^\\.", "").replaceAll("\\.$", "");

        StringBuilder result = new StringBuilder();
        boolean lastCharWasDot = false;
        boolean lastCharWasUnderscore = false;
        for (char c : input.toCharArray()) {
            if (Character.isJavaIdentifierPart(c)) {
                result.append(c);
                lastCharWasDot = false;
                lastCharWasUnderscore = false;
            } else if (c == '.' && !lastCharWasDot) {
                result.append(c);
                lastCharWasDot = true;
                lastCharWasUnderscore = false;
            } else {
                if (!lastCharWasUnderscore) {
                    result.append('_');
                    lastCharWasUnderscore = true;
                }
                lastCharWasDot = false;
            }
        }

        String output = result.toString();
        if (Character.isDigit(output.charAt(0))) {
            output = "_" + output;
        }

        String[] keywords = {"abstract", "assert", "boolean", "break", "byte", "case",
                "catch", "char", "class", "const", "continue", "default",
                "do", "double", "else", "enum", "extends", "final", "finally",
                "float", "for", "goto", "if", "implements", "import", "instanceof",
                "int", "interface", "long", "native", "new", "package", "private",
                "protected", "public", "return", "short", "static", "strictfp",
                "super", "switch", "synchronized", "this", "throw", "throws",
                "transient", "try", "void", "volatile", "while"};

        for (String keyword : keywords) {
            if (output.equals(keyword)) {
                output = "_" + output;
                break;
            }
        }

        return output;
    }
}
