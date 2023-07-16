package com.example.codewarsplugin;

import com.example.codewarsplugin.state.views.LoginView;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManagerListener;

public class MyProjectManagerListener implements ProjectManagerListener {
    @Override
    public void projectOpened(Project project) {
        System.out.println("Project opened");
    }
}
