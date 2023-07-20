package com.example.codewarsplugin.state;

import com.example.codewarsplugin.SidePanel;
import com.example.codewarsplugin.views.View;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;

public class StateParams {

    private Project project;
    private ToolWindow toolWindow;
    private Store store;
    private SidePanel sidePanel;
    private View currentView;

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public ToolWindow getToolWindow() {
        return toolWindow;
    }

    public void setToolWindow(ToolWindow toolWindow) {
        this.toolWindow = toolWindow;
    }

    public Store getVars() {
        return store;
    }

    public void setVars(Store store) {
        this.store = store;
    }

    public SidePanel getSidePanel() {
        return sidePanel;
    }

    public void setSidePanel(SidePanel sidePanel) {
        this.sidePanel = sidePanel;
    }


    public View getCurrentView() {
        return currentView;
    }

    public void setCurrentView(View currentView) {
        this.currentView = currentView;
    }
}
