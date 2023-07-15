package com.example.codewarsplugin;

import com.example.codewarsplugin.state.views.LoginView;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManagerListener;

public class MyProjectManagerListener implements ProjectManagerListener {
    @Override
    public void projectOpened(Project project) {
        // Load your plugin and initialize the side panel here
        // For simplicity, let's assume MyPlugin is the class representing your plugin
        System.out.println("Project opened");
    }
}
