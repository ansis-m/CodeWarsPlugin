package com.example.codewarsplugin.services.files.create;

import com.example.codewarsplugin.models.kata.KataDirectory;
import com.example.codewarsplugin.models.kata.KataInput;
import com.example.codewarsplugin.models.kata.KataRecord;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

import java.util.Arrays;
import java.util.stream.Collectors;

public class PythonFileService extends AbstractFileService{
    public PythonFileService(KataInput input, KataRecord record, Project project) {
        super(input, record, project);
    }

    @Override
    public String getFileName() {
        return null;
    }

    @Override
    protected String getRecordFileName() {
        return null;
    }

    @Override
    protected String getInputFileName() {
        return null;
    }

    @Override
    public void createWorkFile() {
        throw new RuntimeException("not implemented!");
    }

    @Override
    public void createTestFile() {
        throw new RuntimeException("not implemented!");

    }

    @Override
    public String getDirectoryName() {
        return "codewars_" +
                Arrays.stream(record.getSlug().split("-"))
                        .map(String::toLowerCase)
                        .map(PythonFileService::sanitizeForPackageName)
                        .collect(Collectors.joining("_"));
    }

    @Override
    public void createRecordFile() {

    }

    @Override
    public void createInputFile() {

    }

    @Override
    public String getFileBaseName(String input) {
        return null;
    }

    @Override
    public String getTestFileName() {
        return null;
    }

    @Override
    public KataDirectory createKataDirectory() {

        return null;
    }

    @Override
    public void initDirectory() {

        try {
            VirtualFile file = directory.createChildData(this, "__init__.py");
            file.refresh(false, true);

        } catch (Exception ignored){}

    }

    public static String sanitizeForPackageName(String input) {
        if (input == null || input.isEmpty()) {
            return "package";
        }

        input = input.trim().replaceAll("^\\.", "").replaceAll("\\.$", "");

        StringBuilder result = new StringBuilder();
        boolean lastCharWasUnderscore = true;
        for (char c : input.toCharArray()) {
            if (Character.isJavaIdentifierPart(c)) {
                if (!lastCharWasUnderscore || c != '_'){
                    result.append(c);
                    lastCharWasUnderscore = c == '_';
                }

            } else {
                if (!lastCharWasUnderscore) {
                    result.append('_');
                    lastCharWasUnderscore = true;
                }
            }
        }
        return result.length() > 0? result.toString() : "def";
    }
}
