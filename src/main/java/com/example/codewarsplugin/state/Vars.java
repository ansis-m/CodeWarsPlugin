package com.example.codewarsplugin.state;

import com.example.codewarsplugin.SidePanel;
import com.example.codewarsplugin.components.*;
import com.example.codewarsplugin.views.LogedInView;
import com.example.codewarsplugin.views.LoginView;
import com.example.codewarsplugin.views.View;

public class Vars {

    private SidePanel sidePanel;
    private LoginPanel loginPanel;
    private TopImagePanel topImagePanel;
    private KataRecordPanel kataRecordPanel;
    private LogedInView logedInView;
    private LoginView loginView;
    private View currentView;


    public Vars(SidePanel sidePanel){
        this.sidePanel = sidePanel;
        loginPanel = new LoginPanel(this);
        topImagePanel = new TopImagePanel(this);
        kataRecordPanel = new KataRecordPanel(this);
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

    public LoginPanel getLoginManager() {
        return loginPanel;
    }
    public TopImagePanel getTopImagePanel() {
        return topImagePanel;
    }
    public KataRecordPanel getKataPrompt() {
        return kataRecordPanel;
    }
    public SidePanel getSidePanel() {
        return sidePanel;
    }
    public void setSidePanel(SidePanel sidePanel) {
        this.sidePanel = sidePanel;
    }
}
