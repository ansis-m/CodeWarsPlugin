package com.example.codewarsplugin.services.files.create;

import com.example.codewarsplugin.models.kata.KataInput;
import com.example.codewarsplugin.models.kata.KataRecord;
import com.intellij.openapi.project.Project;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JavascriptFileService extends AbstractFileService{

    private final Pattern classPattern = Pattern.compile("(?<!\\.)\\bfunction\\b");
    public JavascriptFileService(KataInput input, KataRecord record, Project project) {
        super(input, record, project);
    }

    @Override
    protected String getRecordFileName() {
        return "record.json";
    }

    @Override
    protected String getInputFileName() {
        return "input.json";
    }

    @Override
    public String getDirectoryName() {
        return "codewars-" + record.getSlug().replaceAll("[^a-zA-Z0-9-_]+", "-").replaceAll("-+", "-");
    }

    @Override
    public String getFileBaseName(String input) {
        Matcher matcher = classPattern.matcher(input);

        if (matcher.find()) {
            int classIndex = matcher.start();
            int startIndex = classIndex + 8;
            int endIndex = input.indexOf('(');
            return endIndex > startIndex? input.substring(startIndex, endIndex).strip().split("[,\\s<]")[0] : "filename";
        } else {
            return "filename";
        }
    }

    @Override
    public String getFileName() {
        return "solution.js";
    }

    @Override
    public String getTestFileName() {
        return "test.js";
    }

    @Override
    public void initDirectory() {
        testDirectory = directory;
        workDirectory = directory;
    }

    @Override
    public byte[] getFileContent(boolean isWorkFile) {
        return (isWorkFile? input.getSetup() + getExport() : getImportMessage() + input.getExampleFixture()).getBytes();
    }

    private String getImportMessage() {
        return "// Install Mocha to run tests locally!" +
                " Run \"npm install mocha --save-dev\".\n\n";
    }

    private String getExport() {
        return String.format("\n\nmodule.exports = %s;  //required only to run tests locally", getFileBaseName(input.getSetup()));
    }
}
