package com.example.codewarsplugin.state;

import com.example.codewarsplugin.SidePanel;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;

public class StateParams {

    private Project project;
    private ToolWindow toolWindow;
    private Vars vars;
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

    public Vars getVars() {
        return vars;
    }

    public void setVars(Vars vars) {
        this.vars = vars;
    }

    public SidePanel getSidePanel() {
        return sidePanel;
    }

    public void setSidePanel(SidePanel sidePanel) {
        this.sidePanel = sidePanel;
    }
}
