package com.example.codewarsplugin.state.views;

import com.example.codewarsplugin.SidePanel;

public interface View {


    public static boolean setup(){
        return true;
    }

    public static boolean cleanUp(){
        return true;
    }
}
