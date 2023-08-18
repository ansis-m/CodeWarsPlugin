package com.cw.codewarsplugin.services.files.create;

import com.cw.codewarsplugin.config.StringConstants;
import com.cw.codewarsplugin.exceptions.ModuleNotFoundException;
import com.cw.codewarsplugin.models.kata.KataInput;
import com.cw.codewarsplugin.models.kata.KataRecord;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JavascriptFileService extends AbstractFileService{

    private final Pattern classPattern = Pattern.compile("(?<!\\.)\\bfunction\\b");
    public JavascriptFileService(KataInput input, KataRecord record, Project project) {
        super(input, record, project);
    }

    @Override
    public String getDirectoryName() {
        return "codewars-js-" + record.getSlug().replaceAll("[^a-zA-Z0-9-_]+", "-").replaceAll("-+", "-");
    }


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
    public boolean shouldAddModule(@NotNull ModuleType<?> moduleType) {
        return moduleType.getName().toLowerCase().contains("web");
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

    static String getImportMessage() {
        return "// Install Mocha to run tests locally!" +
                " Run \"npm install mocha --save-dev\"." +
                "\n// Install Chai: \"npm install chai --save-dev\"\n\n";
    }

    private String getExport() {
        return String.format("\n\nmodule.exports = %s;  //required only to run tests locally", getFileBaseName(input.getSetup()));
    }

    @Override
    public void throwModuleNotFoundException(String language) {
        throw new ModuleNotFoundException(StringConstants.JS_MODULE_NOT_FOUND);
    }
}
