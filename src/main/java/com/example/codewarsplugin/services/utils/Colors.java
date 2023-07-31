package com.example.codewarsplugin.services.utils;

import com.intellij.ui.JBColor;

import java.awt.*;

public class Colors {

    public static JBColor getColor(String color){

        switch (color) {
            case ("yellow"):
                return new JBColor(Color.decode("#ecb613"), Color.decode("#ecb613"));
            case ("purple"):
                return new JBColor(Color.decode("#866cc7"), Color.decode("#866cc7"));
            case ("blue"):
                return new JBColor(Color.decode("#1f87e7"), Color.decode("#1f87e7"));
            default:
                return new JBColor(Color.decode("#363535"), Color.decode("#bbbbbb"));
        }
    }

    public static JBColor getColor(int rank) {

        switch (rank) {
            case 5:
            case 6:
                return getColor("yellow");
            case 1:
            case 2:
                return getColor("purple");
            case 3:
            case 4:
                return getColor("blue");
            default:
                return getColor("");
        }
    }
}
