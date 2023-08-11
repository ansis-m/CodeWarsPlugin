package com.example.codewarsplugin.models.kata;

import com.intellij.openapi.vfs.VirtualFile;

public class KataDirectory {

    private VirtualFile directory;
    private VirtualFile testDirectory;
    private VirtualFile workDirectory;
    private VirtualFile metaDataDirectory;
    private VirtualFile workFile;
    private VirtualFile testFile;
    private KataRecord record;
    private KataInput input;


    //toString method used in the selector panel.
    @Override
    public String toString() {
        return record.getName() + " in " + record.getSelectedLanguage();
    }

    public VirtualFile getDirectory() {
        return directory;
    }

    public void setDirectory(VirtualFile directory) {
        this.directory = directory;
    }

    public VirtualFile getMetaDataDirectory() {
        return metaDataDirectory;
    }

    public void setMetaDataDirectory(VirtualFile metaDataDirectory) {
        this.metaDataDirectory = metaDataDirectory;
    }

    public VirtualFile getWorkFile() {
        return workFile;
    }

    public void setWorkFile(VirtualFile workFile) {
        this.workFile = workFile;
    }

    public VirtualFile getTestFile() {
        return testFile;
    }

    public void setTestFile(VirtualFile testFile) {
        this.testFile = testFile;
    }

    public KataRecord getRecord() {
        return record;
    }

    public void setRecord(KataRecord record) {
        this.record = record;
    }

    public KataInput getInput() {
        return input;
    }

    public void setInput(KataInput input) {
        this.input = input;
    }

    public boolean isComplete() {
        return directory != null
                && metaDataDirectory != null
                && workDirectory != null
                && testDirectory != null
                && record != null
                && input != null
                && workFile != null;
    }

    public VirtualFile getTestDirectory() {
        return testDirectory;
    }

    public void setTestDirectory(VirtualFile testDirectory) {
        this.testDirectory = testDirectory;
    }

    public VirtualFile getWorkDirectory() {
        return workDirectory;
    }

    public void setWorkDirectory(VirtualFile workDirectory) {
        this.workDirectory = workDirectory;
    }

    public boolean isTheSame(KataDirectory directory) {
        return directory != null && directory.getRecord().equals(this.record);
    }
}
