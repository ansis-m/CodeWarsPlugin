package com.example.codewarsplugin.state;

import com.example.codewarsplugin.components.*;

import javax.swing.*;

public class Panels {

    private static JPanel sidePanel;
    private static final LoginManager loginManager = new LoginManager();
    private static final TopImagePanel topImagePanel = new TopImagePanel();
    private static final KataPrompt kataPrompt = new KataPrompt();

    public static LoginManager getLoginManager() {
        return loginManager;
    }
    public static TopImagePanel getTopImagePanel() {
        return topImagePanel;
    }
    public static KataPrompt getKataPrompt() {
        return kataPrompt;
    }
    public static JPanel getSidePanel() {
        return sidePanel;
    }

    public static void setSidePanel(JPanel sidePanel) {
        Panels.sidePanel = sidePanel;
    }
}
