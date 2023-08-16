package com.example.codewarsplugin.services.files.create;

import com.example.codewarsplugin.exceptions.ModuleNotFoundException;
import com.example.codewarsplugin.models.kata.KataInput;
import com.example.codewarsplugin.models.kata.KataRecord;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.example.codewarsplugin.config.StringConstants.*;
import static com.example.codewarsplugin.services.files.create.JavascriptFileService.getImportMessage;

public class TypescriptFileService extends AbstractFileService{
    private final Pattern classPattern = Pattern.compile("(?<!\\.)\\bfunction\\b");
    public TypescriptFileService(KataInput input, KataRecord record, Project project) {
        super(input, record, project);
    }

    @Override
    public String getDirectoryName() {
        return "codewars-ts-" + record.getSlug().replaceAll("[^a-zA-Z0-9-_]+", "-").replaceAll("-+", "-");
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
        return "solution.ts";
    }

    @Override
    public String getTestFileName() {
        return "test.ts";
    }

    @Override
    public void initDirectory() {

        try {
            VirtualFile file = directory.createChildData(this, TS_CONFIG);
            file.refresh(false, true);
            file.setBinaryContent((
                    "{\n" +
                    "  \"compilerOptions\": {\n" +
                    "    \"module\": \"commonjs\",\n" +
                    "    \"target\": \"es6\",\n" +
                    "    \"sourceMap\": true\n" +
                    "  },\n" +
                    "  \"exclude\": [\n" +
                    "    \"node_modules\"\n" +
                    "  ]\n" +
                    "}").getBytes());

        } catch (Exception ignored){}
        testDirectory = directory;
        workDirectory = directory;
    }

    @Override
    public byte[] getFileContent(boolean isWorkFile) {
        return (isWorkFile? input.getSetup() : getImportMessage() + input.getExampleFixture()).getBytes();
    }

    @Override
    public void throwModuleNotFoundException(String language) {
        throw new ModuleNotFoundException(JS_MODULE_NOT_FOUND);
    }
}
