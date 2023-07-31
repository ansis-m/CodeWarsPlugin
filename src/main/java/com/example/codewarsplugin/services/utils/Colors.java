package com.example.codewarsplugin.services.utils;

import java.awt.*;

public class Colors {

    public static Color getColor(String color){

        switch (color) {
            case ("yellow"):
                return Color.decode("#ecb613");
            case ("purple"):
                return Color.decode("#866cc7");
            case ("blue"):
                return Color.decode("#1f87e7");
            default:
                return Color.decode("#e6e6e6");
        }
    }

}
