package com.example.codewarsplugin.services.project;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;

public class MyProjectManager {

    private static Project project;
    private static ToolWindow toolWindow;

    public static void init(Project project, ToolWindow toolWindow) {
        MyProjectManager.project = project;
        MyProjectManager.toolWindow = toolWindow;
    }

    public static Project getProject() {
        return project;
    }

    public static ToolWindow getToolWindow() {
        return toolWindow;
    }
}
