package com.example.codewarsplugin.state;

import com.example.codewarsplugin.state.views.View;
import lombok.Data;

@Data
public class ApplicationState {
    private static View currentView;


    public static void setView(View view) {
        currentView = view;
    }

    public static View getView() {
        return currentView;
    }

}
