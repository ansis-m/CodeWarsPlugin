package com.example.codewarsplugin.state;

import com.example.codewarsplugin.components.*;
import com.example.codewarsplugin.state.views.LogedInView;
import com.example.codewarsplugin.state.views.LoginView;

import javax.swing.*;

public class Panels {

    private static JPanel sidePanel;
    private static LoginManager loginManager;
    private static TopImagePanel topImagePanel;
    private static KataPrompt kataPrompt;
    private static LogedInView logedInView;
    private static LoginView loginView;

    public static void init(){
        loginManager = new LoginManager();
        topImagePanel = new TopImagePanel();
        kataPrompt = new KataPrompt();
        logedInView = new LogedInView();
        loginView = new LoginView();
    }

    public static LogedInView getLogedInView() {
        return logedInView;
    }

    public static LoginView getLoginView() {
        return loginView;
    }

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
