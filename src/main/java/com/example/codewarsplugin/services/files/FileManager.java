package com.example.codewarsplugin.services.files;

import com.example.codewarsplugin.models.kata.KataInput;
import com.example.codewarsplugin.models.kata.KataRecord;


public class FileManager {
    public  void createFile(KataInput input, KataRecord record, FileServiceClient client) {

        FileService service = FileServiceFactory.createFileService(input, record, client);
        service.getSourcesRoot();
        service.createDirectory();
        service.createTestFile();
        service.createWorkFile();
        service.createRecordFile();
        service.createInputFile();
    }
}
