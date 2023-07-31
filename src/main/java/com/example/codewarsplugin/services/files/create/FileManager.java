package com.example.codewarsplugin.services.files.create;

import com.example.codewarsplugin.models.kata.KataInput;
import com.example.codewarsplugin.models.kata.KataRecord;
import com.example.codewarsplugin.services.files.parse.KataDirectoryParser;
import com.example.codewarsplugin.state.Store;


public class FileManager {
    public void createFile(KataInput input, KataRecord record, Store store, FileServiceClient client) {

        FileService service = FileServiceFactory.createFileService(input, record);
        service.getSourcesRoot();
        service.createDirectory();
        service.createTestFile();
        service.createWorkFile();
        service.createRecordFile();
        service.createInputFile();
        var directory = service.createKataDirectory();
        if (directory.isComplete()) {
            store.setCurrentKataDirectory(directory);
            client.transitionToWorkView(directory);
        } else {
            client.notifyKataDirectoryCreationFailed();
        }
    }
}
