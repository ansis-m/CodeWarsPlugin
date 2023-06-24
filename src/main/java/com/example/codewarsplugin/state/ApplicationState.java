package com.example.codewarsplugin.state;

import com.example.codewarsplugin.state.views.View;


public class ApplicationState {
    private static View currentView;
    private static View nextView;
    private static View previousView;


    public static void setView(View view) {
        currentView = view;
    }

    public static View getView() {
        return currentView;
    }
}
