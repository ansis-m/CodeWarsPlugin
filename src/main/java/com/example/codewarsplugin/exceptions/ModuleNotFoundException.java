package com.example.codewarsplugin.exceptions;

public class ModuleNotFoundException extends RuntimeException {

    public ModuleNotFoundException(String message) {
        super(message);
    }
}
