package com.example.codewarsplugin;

import com.example.codewarsplugin.services.WebDriver;
import com.example.codewarsplugin.services.project.ProjectManager;
import com.example.codewarsplugin.state.ApplicationState;
import com.example.codewarsplugin.state.Panels;
import com.example.codewarsplugin.state.views.LogedInView;
import com.example.codewarsplugin.state.views.LoginView;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;

import javax.swing.*;

//177, 54, 30
public class SidePanel extends JPanel {

    public SidePanel(Project project, ToolWindow toolWindow) {
        Panels.setSidePanel(this);
        WebDriver.init();
        if (LoginView.setup()){
            ApplicationState.setView(LoginView.class);
        }
        ProjectManager.init(project, toolWindow);
    }

}
