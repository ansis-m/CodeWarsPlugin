package com.example.codewarsplugin.state;

import com.example.codewarsplugin.SidePanel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;

public class StateParams {

    private Project project;
    private ToolWindow toolWindow;
    private Store store;
    private SidePanel sidePanel;

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

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public SidePanel getSidePanel() {
        return sidePanel;
    }

    public void setSidePanel(SidePanel sidePanel) {
        this.sidePanel = sidePanel;
    }

}
