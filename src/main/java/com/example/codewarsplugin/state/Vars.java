package com.example.codewarsplugin.state;

import com.example.codewarsplugin.SidePanel;
import com.example.codewarsplugin.components.*;
import com.example.codewarsplugin.views.LogedInView;
import com.example.codewarsplugin.views.LoginView;
import com.example.codewarsplugin.views.View;

public class Vars {

    private SidePanel sidePanel;
    private LoginManager loginManager;
    private TopImagePanel topImagePanel;
    private KataPrompt kataPrompt;
    private LogedInView logedInView;
    private LoginView loginView;
    private View currentView;


    public Vars(SidePanel sidePanel){
        this.sidePanel = sidePanel;
        loginManager = new LoginManager(this);
        topImagePanel = new TopImagePanel(this);
        kataPrompt = new KataPrompt(this);
        logedInView = new LogedInView(this);
        loginView = new LoginView(this);
    }

    public View getCurrentView() {
        return currentView;
    }

    public void setCurrentView(View currentView) {
        this.currentView = currentView;
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
    public SidePanel getSidePanel() {
        return sidePanel;
    }
    public void setSidePanel(SidePanel sidePanel) {
        this.sidePanel = sidePanel;
    }
}
