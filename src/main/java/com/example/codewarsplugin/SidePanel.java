package com.example.codewarsplugin;

import com.example.codewarsplugin.services.login.LoginService;
import com.example.codewarsplugin.services.login.WebDriver;
import com.example.codewarsplugin.services.project.MyProjectManager;
import com.example.codewarsplugin.state.StateParams;
import com.example.codewarsplugin.state.SyncService;
import com.example.codewarsplugin.state.Vars;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.wm.ToolWindow;

import javax.swing.*;
import java.awt.*;

//177, 54, 30
public class SidePanel extends JPanel {
    private Vars vars;
    private StateParams stateParams;

    public SidePanel(Project project, ToolWindow toolWindow) {
        setLayout(new BorderLayout());
        initPlugin(project, toolWindow);
        vars.getCurrentView().setup();
        MyProjectManager.init(project, toolWindow);
        ProjectManager.getInstance().addProjectManagerListener(new MyProjectManagerListener()); //kautkaada servisa metode
    }

    private void initPlugin(Project project, ToolWindow toolWindow) {
        vars = new Vars(this);
        WebDriver.init();

        stateParams = new StateParams();
        stateParams.setProject(project);
        stateParams.setToolWindow(toolWindow);
        stateParams.setSidePanel(this);
        stateParams.setVars(vars);
        stateParams.setCurrentView(LoginService.loginSuccess? vars.getLogedInView() : vars.getLoginView());
        vars.setCurrentView(LoginService.loginSuccess? vars.getLogedInView() : vars.getLoginView());

        SyncService.addParams(stateParams);

        Runtime.getRuntime().addShutdownHook(new Thread(this::cleanup));
    }

    private void cleanup() {
        WebDriver.quit();
    }

    public StateParams getStateParams() {
        return stateParams;
    }
}
