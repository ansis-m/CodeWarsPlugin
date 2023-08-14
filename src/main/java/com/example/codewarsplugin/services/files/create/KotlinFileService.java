package com.example.codewarsplugin.services.files.create;

import com.example.codewarsplugin.models.kata.KataInput;
import com.example.codewarsplugin.models.kata.KataRecord;
import com.intellij.openapi.project.Project;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class KotlinFileService extends AbstractFileService{
    public KotlinFileService(KataInput input, KataRecord record, Project project) {
        super(input, record, project);
    }
    private final Pattern classPattern = Pattern.compile("(?<!\\.)\\bclass\\b");
    private final Pattern functionPattern = Pattern.compile("(?<!\\.)\\bfun\\b");
    private final Pattern objectPattern = Pattern.compile("(?<!\\.)\\bobject\\b");

    @Override
    public byte[] getFileContent(boolean isWorkFile) {
        return (getPackage() + (isWorkFile? input.getSetup() : input.getExampleFixture())).getBytes();
    }

    private String getPackage() {
        return input.getSetup().contains("package")? "" : "package " + getDirectoryName() + ";\n\n";
    }

    @Override
    public String getTestFileName() {
        return getFileBaseName(input.getExampleFixture()) + ".kt";
    }

    @Override
    public void initDirectory() {
        testDirectory = directory;
        workDirectory = directory;
    }

    @Override
    public String getFileName() {
        return getFileBaseName(input.getSetup()) + ".kt";
    }


    public String getFileBaseName(String input){

        final Matcher classMatcher = classPattern.matcher(input);
        final Matcher functionMatcher = functionPattern.matcher(input);
        final Matcher objectMatcher = objectPattern.matcher(input);

        if (classMatcher.find()) {
            int classIndex = classMatcher.start();
            int startIndex = classIndex + 5;
            int endIndex = input.indexOf('{');
            return endIndex > startIndex? input.substring(startIndex, endIndex).strip().split("[,\\s<]")[0] : "filename";
        } else if (objectMatcher.find()) {
            int objectIndex = objectMatcher.start();
            int startIndex = objectIndex + 6;
            int endIndex = input.indexOf('{');
            return endIndex > startIndex? input.substring(startIndex, endIndex).strip().split("[,\\s<]")[0] : "filename";
        } else if (functionMatcher.find()) {
            int functionIndex = functionMatcher.start();
            int startIndex = functionIndex + 3;
            int endIndex = input.indexOf('(');
            return endIndex > startIndex? input.substring(startIndex, endIndex).strip().split("[,\\s<]")[0] : "filename";
        } else {
            return "filename";
        }
    }

    @Override
    public String getDirectoryName() {
        return "codewars.kotlin." +
                Arrays.stream(record.getSlug().split("-"))
                        .map(JavaFileService::sanitizeForPackageName)
                        .collect(Collectors.joining("."));
    }
}
