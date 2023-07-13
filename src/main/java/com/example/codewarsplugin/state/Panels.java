package com.example.codewarsplugin.state;

import com.example.codewarsplugin.components.*;

public class Panels {

    private static final LoginManager loginManager = new LoginManager();
    private static final TopImagePanel topImagePanel = new TopImagePanel();
    private static final TitlePanel titlePanel = new TitlePanel();
    private static final KataPrompt kataPrompt = new KataPrompt();

    public static LoginManager getLoginManager() {
        return loginManager;
    }
    public static TopImagePanel getTopImagePanel() {
        return topImagePanel;
    }
    public static TitlePanel getTitlePanel() {
        return titlePanel;
    }
    public static KataPrompt getKataPrompt() {
        return kataPrompt;
    }
}
