package com.example.codewarsplugin;

import com.example.codewarsplugin.services.WebDriver;
import com.example.codewarsplugin.services.project.ProjectManager;
import com.example.codewarsplugin.state.ApplicationState;
import com.example.codewarsplugin.state.views.View;
import com.example.codewarsplugin.state.views.LoginView;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;

import javax.swing.*;

//177, 54, 30
public class SidePanel extends JPanel {

    public SidePanel(Project project, ToolWindow toolWindow) {
        View loginView = new LoginView(this);
        if (loginView.setup()){
            ApplicationState.setView(loginView);
        }
        ProjectManager.init(project, toolWindow);
        WebDriver.init();
    }
}
