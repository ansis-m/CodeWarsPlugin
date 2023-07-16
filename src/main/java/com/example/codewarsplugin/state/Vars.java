package com.example.codewarsplugin.state;

import com.example.codewarsplugin.components.*;
import com.example.codewarsplugin.views.LogedInView;
import com.example.codewarsplugin.views.LoginView;


import javax.swing.*;

public class Vars {

    private JPanel sidePanel;
    private LoginManager loginManager;
    private TopImagePanel topImagePanel;
    private KataPrompt kataPrompt;
    private LogedInView logedInView;
    private LoginView loginView;

    public void init(){
        loginManager = new LoginManager(this);
        topImagePanel = new TopImagePanel(this);
        kataPrompt = new KataPrompt(this);
        logedInView = new LogedInView(this);
        loginView = new LoginView(this);
    }

    public LogedInView getLogedInView() {
        return logedInView;
    }

    public LoginView getLoginView() {
        return loginView;
    }

    public LoginManager getLoginManager() {
        return loginManager;
    }
    public TopImagePanel getTopImagePanel() {
        return topImagePanel;
    }
    public KataPrompt getKataPrompt() {
        return kataPrompt;
    }
    public JPanel getSidePanel() {
        return sidePanel;
    }
    public void setSidePanel(JPanel sidePanel) {
        this.sidePanel = sidePanel;
    }
}
