package com.example.codewarsplugin.state.views;

import com.example.codewarsplugin.SidePanel;

public abstract class View {

    public static SidePanel sidePanel;

    public View(SidePanel sidePanel){
        View.sidePanel = sidePanel;
    }
    public View(){}

    public static boolean setup(){
        return true;
    }

    public static boolean cleanUp(){
        return true;
    }
}
