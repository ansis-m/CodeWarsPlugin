package com.example.codewarsplugin.state;

import com.example.codewarsplugin.components.LoginManager;
import com.example.codewarsplugin.components.TitlePanel;
import com.example.codewarsplugin.components.TopImagePanel;
import com.example.codewarsplugin.components.WaitPanel;

public class Panels {

    private static final LoginManager loginManager = new LoginManager();
    private static final TopImagePanel topImagePanel = new TopImagePanel();
    private static final TitlePanel titlePanel = new TitlePanel();
    private static final WaitPanel waitPanel = new WaitPanel();

    public static LoginManager getLoginManager() {
        return loginManager;
    }

    public static TopImagePanel getTopImagePanel() {
        return topImagePanel;
    }

    public static TitlePanel getTitlePanel() {
        return titlePanel;
    }
    public static WaitPanel getWaitPanel() {
        return waitPanel;
    }
}
