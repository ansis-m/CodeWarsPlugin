package com.example.codewarsplugin.services.files.create;

import com.example.codewarsplugin.models.kata.KataInput;
import com.example.codewarsplugin.models.kata.KataRecord;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.stream.Collectors;

import static com.example.codewarsplugin.config.StringConstants.*;

public class RubyFileService extends AbstractFileService{

    public RubyFileService(KataInput input, KataRecord record, Project project) {
        super(input, record, project);
    }

    @Override
    public String getFileName() {
        return "solution.rb";
    }

    @Override
    protected boolean shouldAddModule(@NotNull ModuleType<?> moduleType) {
        return moduleType.getName().toLowerCase().contains("ruby");
    }

    @Override
    public String getDirectoryName() {
        return "codewars_" + getFileBaseName(record.getSlug());

    }

    public String getFileBaseName(String input) {
        return Arrays.stream(input.split("-"))
                .map(String::toLowerCase)
                .map(PythonFileService::sanitizeForPackageName)
                .collect(Collectors.joining("_"));
    }

    @Override
    public String getTestFileName() {
        return "test.rb";
    }

    @Override
    public void initDirectory() {
        workDirectory = directory;
        testDirectory = directory;
    }

    @Override
    public byte[] getFileContent(boolean isWorkFile) {
        return (isWorkFile? input.getSetup() : RUBY_TEST_FRAMEWORK_MESSAGE + input.getExampleFixture()).getBytes();
    }
}
