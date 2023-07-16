package com.example.codewarsplugin;

import com.example.codewarsplugin.services.LoginService;
import com.example.codewarsplugin.services.WebDriver;
import com.example.codewarsplugin.services.project.MyProjectManager;
import com.example.codewarsplugin.state.ApplicationState;
import com.example.codewarsplugin.state.Panels;
import com.example.codewarsplugin.state.views.LoginView;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.wm.ToolWindow;

import javax.swing.*;
import java.awt.*;

//177, 54, 30
public class SidePanel extends JPanel {

    public SidePanel(Project project, ToolWindow toolWindow) {
        Panels panels = new Panels();
        panels.init();
        panels.setSidePanel(this);
        WebDriver.init();
        Runtime.getRuntime().addShutdownHook(new Thread(this::cleanup));
        if (!LoginService.loginSuccess){
            panels.getLoginView().setup();
            ApplicationState.setView(LoginView.class);
        } else {
            setLayout(new BorderLayout());
            panels.getLogedInView().init();
            panels.getLogedInView().setup();
        }
        MyProjectManager.init(project, toolWindow);
        ProjectManager.getInstance().addProjectManagerListener(new MyProjectManagerListener()); //kautkaada servisa metode
//        Project project1 = ProjectManager.getInstance().getOpenProjects()[0];
//
//        // Retrieve the projects directory for the active project
//        Path projectsDir = ProjectUtil.guessProjectDir(project1).toNioPath();
//        System.out.println("IntelliJ IDEA Projects Directory: " + projectsDir);
    }

    private void cleanup() {
        WebDriver.quit();
    }

}
