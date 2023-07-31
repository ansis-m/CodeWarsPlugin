package com.example.codewarsplugin.state;

import com.example.codewarsplugin.SidePanel;
import com.example.codewarsplugin.components.*;
import com.example.codewarsplugin.models.kata.KataDirectory;
import com.example.codewarsplugin.views.LogedInView;
import com.example.codewarsplugin.views.LoginView;
import com.example.codewarsplugin.views.View;
import com.example.codewarsplugin.views.WorkView;

import java.util.LinkedList;

public class Store {

    private SidePanel sidePanel;
    private LoginPanel loginPanel;
    private LogedInView logedInView;
    private LoginView loginView;
    private WorkView workView;
    private View currentView;
    private LinkedList<View> previousViews = new LinkedList<>();
    private KataDirectory directory;

    public Store(SidePanel sidePanel){
        this.sidePanel = sidePanel;
        logedInView = new LogedInView(this);
        loginView = new LoginView(this);
        workView = new WorkView(this);
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

    public LoginPanel getLoginPanel() {
        return loginPanel;
    }
    public SidePanel getSidePanel() {
        return sidePanel;
    }
    public void setSidePanel(SidePanel sidePanel) {
        this.sidePanel = sidePanel;
    }

    public WorkView getWorkView() {
        return workView;
    }
    public void setWorkView(WorkView workView) {
        this.workView = workView;
    }

    public View getPreviousView() {
        return previousViews.getLast();
    }

    public LinkedList<View> getPreviousViews() {
        return previousViews;
    }

    public void setPreviousViews(LinkedList<View> previousViews) {
        this.previousViews = previousViews;
    }

    public void setCurrentKataDirectory(KataDirectory directory) {
        this.directory = directory;
    }

    public KataDirectory getDirectory() {
        return directory;
    }

    public void setLoginPanel(LoginPanel loginPanel) {
        this.loginPanel = loginPanel;
    }
}
