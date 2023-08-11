package com.example.codewarsplugin.services.files.create;

import com.example.codewarsplugin.exceptions.LanguageNotSupportedException;
import com.example.codewarsplugin.models.kata.KataInput;
import com.example.codewarsplugin.models.kata.KataRecord;
import com.intellij.openapi.project.Project;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import static org.apache.commons.lang.WordUtils.capitalize;

public class FileServiceFactory {

    public static final List<String> SUPPORTED_LANGUAGES = List.of("java", "python", "javascript");

    private final static String PACKAGE = AbstractFileService.class.getPackageName();
    public static AbstractFileService createFileService(KataInput input, KataRecord record, Project project) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, LanguageNotSupportedException {

        SUPPORTED_LANGUAGES.stream()
                .filter(language -> language.equals(input.getLanguageName()))
                .findFirst()
                .orElseThrow(() -> new LanguageNotSupportedException("Sorry, " + input.getLanguageName() + " is not supported. Codewars plugin currently supports " + String.join(", ", SUPPORTED_LANGUAGES)));

        String className = capitalize(input.getLanguageName()) + "FileService";
        Class<?> serviceClass = Class.forName(PACKAGE + "." + className);
        Constructor<?> constructor = serviceClass.getDeclaredConstructor(KataInput.class, KataRecord.class, Project.class);
        return (AbstractFileService) constructor.newInstance(input, record, project);

    }

}
