package com.example.codewarsplugin.services.files;

import com.example.codewarsplugin.models.kata.KataInput;
import com.example.codewarsplugin.models.kata.KataRecord;

import java.lang.reflect.Constructor;
import java.util.Arrays;

import static org.apache.commons.lang.WordUtils.capitalize;

public class FileServiceFactory {
    private final static String PACKAGE = FileService.class.getPackageName();
    public static FileService createFileService(KataInput input, KataRecord record, FileServiceClient client){

        try{
            String className = capitalize(input.getLanguageName()) + "FileService";
            Class<?> serviceClass = Class.forName(PACKAGE + "." + className);
            Constructor<?> constructor = serviceClass.getDeclaredConstructor(KataInput.class, KataRecord.class, FileServiceClient.class);
            return (FileService) constructor.newInstance(input, record, client);
        } catch (Exception e) {
            System.out.println("class name: " + capitalize(input.getLanguageName()) + "FileService");
            System.out.println("full name: " + PACKAGE + ".");
            Arrays.stream(e.getStackTrace()).forEach(System.out::println);
        }
        return (FileService) new JavaFileService(input, record, client);
    }
}
