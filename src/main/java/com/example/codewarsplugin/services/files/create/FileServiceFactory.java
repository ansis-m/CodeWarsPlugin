package com.example.codewarsplugin.services.files.create;

import com.example.codewarsplugin.models.kata.KataInput;
import com.example.codewarsplugin.models.kata.KataRecord;
import com.intellij.openapi.project.Project;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.apache.commons.lang.WordUtils.capitalize;

public class FileServiceFactory {




    private final static String PACKAGE = FileService.class.getPackageName();
    public static FileService createFileService(KataInput input, KataRecord record, Project project) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {

            String className = capitalize(input.getLanguageName()) + "FileService";
            Class<?> serviceClass = Class.forName(PACKAGE + "." + className);
            Constructor<?> constructor = serviceClass.getDeclaredConstructor(KataInput.class, KataRecord.class, Project.class);
            return (FileService) constructor.newInstance(input, record, project);

    }

}
