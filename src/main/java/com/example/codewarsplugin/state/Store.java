package com.example.codewarsplugin.state;

import com.example.codewarsplugin.SidePanel;
import com.example.codewarsplugin.components.*;
import com.example.codewarsplugin.services.files.parse.KataDirectoryParser;
import com.example.codewarsplugin.views.LogedInView;
import com.example.codewarsplugin.views.LoginView;
import com.example.codewarsplugin.views.View;
import com.example.codewarsplugin.views.WorkView;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;

import java.util.LinkedList;

public class Store {

    private SidePanel sidePanel;
    private LoginPanel loginPanel;
    private TopImagePanel topImagePanel;
    private KataRecordPanel kataRecordPanel;
    private LogedInView logedInView;
    private LoginView loginView;
    private WorkView workView;
    private View currentView;
    private LinkedList<View> previousViews = new LinkedList<>();

    private KataDirectoryParser directoryParser;



    public Store(SidePanel sidePanel){
        this.sidePanel = sidePanel;
        loginPanel = new LoginPanel(this);
        topImagePanel = new TopImagePanel(this);
        kataRecordPanel = new KataRecordPanel(this);
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

    public void parseKataDirectories() {
        directoryParser = new KataDirectoryParser();
        directoryParser.parseSourceDirectories();
    }

    public KataDirectoryParser getDirectoryParser() {
        return directoryParser;
    }
}
