package com.example.codewarsplugin.services.files.create;

import com.example.codewarsplugin.models.kata.JsonSource;
import com.example.codewarsplugin.models.kata.KataInput;
import com.example.codewarsplugin.models.kata.KataRecord;
import com.example.codewarsplugin.services.files.create.AbstractFileService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.Collectors;

public class JavaFileService extends AbstractFileService {


    public JavaFileService(KataInput input, KataRecord record, Project project) {
        super(input, record, project);
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

        if (directory != null) {
            VirtualFile file = null;
            try {
                file = directory.createChildData(this, isWorkFile? getFileName() : getTestFileName());
                file.refresh(false, true);
                file.setBinaryContent(getFileContent(isWorkFile));

                if(isWorkFile) {
                    workFile = file;
                } else {
                    testFile = file;
                }

            } catch (IOException e) {
                e.printStackTrace();
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
        return "codewars.java." +
                Arrays.stream(record.getSlug().split("-"))
                        .map(JavaFileService::sanitizeForPackageName)
                        .collect(Collectors.joining("."));
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

        if (metaData != null) {
            VirtualFile file = null;
            try {
                file = metaData.createChildData(this, filename);
                file.refresh(false, true);
                VirtualFile finalFile = file;
                finalFile.setBinaryContent(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(source).getBytes(StandardCharsets.UTF_8));
            } catch (IOException e) {
                e.printStackTrace();
            }
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
