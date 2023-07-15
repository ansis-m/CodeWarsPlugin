package com.example.codewarsplugin.state;

import com.example.codewarsplugin.state.views.View;


public class ApplicationState {
    private static Class currentView;
    private static Class nextView;
    private static Class previousView;


    public static void setView(Class view) {
        currentView = view;
    }

    public static Class getView() {
        return currentView;
    }
}
