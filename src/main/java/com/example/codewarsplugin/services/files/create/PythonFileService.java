package com.example.codewarsplugin.services.files.create;

import com.example.codewarsplugin.models.kata.KataDirectory;
import com.example.codewarsplugin.models.kata.KataInput;
import com.example.codewarsplugin.models.kata.KataRecord;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

import java.util.Arrays;
import java.util.stream.Collectors;

import static com.example.codewarsplugin.config.StringConstants.INIT_PY;
import static com.example.codewarsplugin.config.StringConstants.PYTHON_TEST_FRAMEWORK_MESSAGE;

public class PythonFileService extends AbstractFileService{
    public PythonFileService(KataInput input, KataRecord record, Project project) {
        super(input, record, project);
    }

    @Override
    public String getFileName() {
        return "solution.py";
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
    public String getDirectoryName() {
        return "codewars_" + getFileBaseName(record.getSlug());

    }

    @Override
    public void createRecordFile() {
        throw new RuntimeException("createRecordFile not implemented in python file service");

    }

    @Override
    public void createInputFile() {
        throw new RuntimeException("createInputFile not implemented in python file service");
    }

    @Override
    public String getFileBaseName(String input) {
        return Arrays.stream(input.split("-"))
                .map(String::toLowerCase)
                .map(PythonFileService::sanitizeForPackageName)
                .collect(Collectors.joining("_"));
    }

    @Override
    public String getTestFileName() {
        return "test.py";
    }

    @Override
    public void initDirectory() {

        try {
            VirtualFile file = directory.createChildData(this, INIT_PY);
            file.refresh(false, true);
            workDirectory = directory;
            testDirectory = directory;
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

    @Override
    public byte[] getFileContent(boolean isWorkFile) {
        return (isWorkFile? input.getSetup() : PYTHON_TEST_FRAMEWORK_MESSAGE + input.getExampleFixture()).getBytes();
    }
}
