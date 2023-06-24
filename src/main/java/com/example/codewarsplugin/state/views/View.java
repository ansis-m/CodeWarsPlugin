package com.example.codewarsplugin.state.views;

import com.example.codewarsplugin.SidePanel;

public abstract class View {

    public static SidePanel sidePanel;

    public View(SidePanel sidePanel){
        View.sidePanel = sidePanel;
    }

    public abstract boolean setup();

    public abstract boolean cleanUp();
}
