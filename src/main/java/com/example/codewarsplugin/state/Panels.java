package com.example.codewarsplugin.state;

import com.example.codewarsplugin.components.LoginManager;
import com.example.codewarsplugin.components.TitlePanel;
import com.example.codewarsplugin.components.TopImagePanel;

public class Panels {

    private static final LoginManager loginManager = new LoginManager();
    private static final TopImagePanel topImagePanel = new TopImagePanel();
    private static final TitlePanel titlePanel = new TitlePanel();

    public static LoginManager getLoginManager() {
        return loginManager;
    }

    public static TopImagePanel getTopImagePanel() {
        return topImagePanel;
    }

    public static TitlePanel getTitlePanel() {
        return titlePanel;
    }
}
