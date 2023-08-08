package com.example.codewarsplugin.services.files.create;

import com.example.codewarsplugin.models.kata.KataInput;
import com.example.codewarsplugin.models.kata.KataRecord;
import com.intellij.openapi.project.Project;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class JavaFileService extends AbstractFileService {

    private final Pattern classPattern = Pattern.compile("(?<!\\.)\\bclass\\b");


    public JavaFileService(KataInput input, KataRecord record, Project project) {
        super(input, record, project);
    }

    @Override
    public byte[] getFileContent(boolean isWorkFile) {
        return (getPackage() + (isWorkFile? input.getSetup() : input.getExampleFixture())).getBytes();
    }

    private String getPackage() {
        return "package " + getDirectoryName() + ";\n\n";
    }

    @Override
    public String getTestFileName() {
        return getFileBaseName(input.getExampleFixture()) + ".java";
    }

    @Override
    public void initDirectory() {
        testDirectory = directory;
        workDirectory = directory;
    }

    @Override
    public String getFileName() {
        return getFileBaseName(input.getSetup()) + ".java";
    }

    @Override
    public String getFileBaseName(String input){

        Matcher matcher = classPattern.matcher(input);

        if (matcher.find()) {
            int classIndex = matcher.start();
            int startIndex = classIndex + 5;
            int endIndex = input.indexOf('{');
            return endIndex > startIndex? input.substring(startIndex, endIndex).strip().split("[,\\s<]")[0] : "filename";
        } else {
            return "filename";
        }
    }

    @Override
    protected String getRecordFileName(){
        return getFileBaseName(input.getSetup()) + "Record.json";
    }

    @Override
    protected String getInputFileName(){
        return getFileBaseName(input.getSetup()) + "Input.json";
    }


    @Override
    public String getDirectoryName() {
        return "codewars.java." +
                Arrays.stream(record.getSlug().split("-"))
                        .map(JavaFileService::sanitizeForPackageName)
                        .collect(Collectors.joining("."));
    }


    public static String sanitizeForPackageName(String input) {
        if (input == null || input.isEmpty()) {
            return "defaultpackage";
        }

        input = input.trim().replaceAll("^\\.", "").replaceAll("\\.$", "");

        StringBuilder result = new StringBuilder();
        boolean lastCharWasDot = false;
        boolean lastCharWasUnderscore = false;
        for (char c : input.toCharArray()) {
            if (Character.isJavaIdentifierPart(c)) {
                result.append(c);
                lastCharWasDot = false;
                lastCharWasUnderscore = false;
            } else if (c == '.' && !lastCharWasDot) {
                result.append(c);
                lastCharWasDot = true;
                lastCharWasUnderscore = false;
            } else {
                if (!lastCharWasUnderscore) {
                    result.append('_');
                    lastCharWasUnderscore = true;
                }
                lastCharWasDot = false;
            }
        }

        String output = result.toString();
        if (Character.isDigit(output.charAt(0))) {
            output = "_" + output;
        }

        String[] keywords = {"abstract", "assert", "boolean", "break", "byte", "case",
                "catch", "char", "class", "const", "continue", "default",
                "do", "double", "else", "enum", "extends", "final", "finally",
                "float", "for", "goto", "if", "implements", "import", "instanceof",
                "int", "interface", "long", "native", "new", "package", "private",
                "protected", "public", "return", "short", "static", "strictfp",
                "super", "switch", "synchronized", "this", "throw", "throws",
                "transient", "try", "void", "volatile", "while"};

        for (String keyword : keywords) {
            if (output.equals(keyword)) {
                output = "_" + output;
                break;
            }
        }

        return output;
    }
}
