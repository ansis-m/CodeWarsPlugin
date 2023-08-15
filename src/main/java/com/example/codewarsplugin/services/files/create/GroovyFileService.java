package com.example.codewarsplugin.services.files.create;

import com.example.codewarsplugin.exceptions.ModuleNotFoundException;
import com.example.codewarsplugin.models.kata.KataInput;
import com.example.codewarsplugin.models.kata.KataRecord;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.example.codewarsplugin.config.StringConstants.GROOVY_MODULE_NOT_FOUND;

public class GroovyFileService extends AbstractFileService{


    private final Pattern classPattern = Pattern.compile("(?<!\\.)\\bclass\\b");
    private final Pattern functionPattern = Pattern.compile("(?<!\\.)\\bdef\\b");

    public GroovyFileService(KataInput input, KataRecord record, Project project) {
        super(input, record, project);
    }

    @Override
    public byte[] getFileContent(boolean isWorkFile) {
        return (isWorkFile? input.getSetup() : input.getExampleFixture()).getBytes();
    }

    @Override
    public String getTestFileName() {
        return getFileBaseName(input.getExampleFixture()) + ".groovy";
    }

    @Override
    public void initDirectory() {
        testDirectory = directory;
        workDirectory = directory;
    }

    @Override
    public String getFileName() {
        return getFileBaseName(input.getSetup()) + ".groovy";
    }

    @Override
    protected boolean shouldAddModule(@NotNull ModuleType<?> moduleType) {
        return moduleType.getName().toLowerCase().contains("java") && !moduleType.getName().toLowerCase().contains("unknown");
    }

    public String getFileBaseName(String input){

        final Matcher classMatcher = classPattern.matcher(input);
        final Matcher functionMatcher = functionPattern.matcher(input);


        if (classMatcher.find()) {
            int classIndex = classMatcher.start();
            int startIndex = classIndex + 5;
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
        return "codewars.groovy." +
                Arrays.stream(record.getSlug().split("-"))
                        .map(JavaFileService::sanitizeForPackageName)
                        .collect(Collectors.joining("."));
    }

    @Override
    public void throwModuleNotFoundException(String language) {
        throw new ModuleNotFoundException(GROOVY_MODULE_NOT_FOUND);
    }

}
