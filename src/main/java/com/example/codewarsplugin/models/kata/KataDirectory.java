package com.example.codewarsplugin.models.kata;

import com.intellij.openapi.vfs.VirtualFile;

public class KataDirectory {

    private VirtualFile directory;
    private VirtualFile metaDataDirectory;
    private VirtualFile workFile;
    private VirtualFile testFile;
    private KataRecord record;
    private KataInput input;

    @Override
    public String toString() {
        return "KataDirectory{" +
                "directory=" + directory.getName() +
                ", metaDataDirectory=" + metaDataDirectory.getName() +
                ", workFile=" + workFile.getName() +
                ", testFile=" + testFile.getName() +
                ", record=" + record.getName() +
                ", input=" + input.getActiveVersion() +
                "}\n";
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
        return directory != null && metaDataDirectory != null && record != null && input != null && workFile != null;
    }
}
