package com.example.codewarsplugin.services.files.create;

import com.example.codewarsplugin.models.kata.KataDirectory;
import com.example.codewarsplugin.models.kata.KataInput;
import com.example.codewarsplugin.models.kata.KataRecord;
import com.intellij.openapi.project.Project;

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
        return null;
    }

    @Override
    public void getSourcesRoot() {

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
    public void getModules() {

    }

}
