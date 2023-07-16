package com.example.codewarsplugin;

import com.example.codewarsplugin.services.LoginService;
import com.example.codewarsplugin.services.WebDriver;
import com.example.codewarsplugin.services.project.MyProjectManager;
import com.example.codewarsplugin.state.StateParams;
import com.example.codewarsplugin.state.StaticVars;
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
        if (!LoginService.loginSuccess){
            vars.getLoginView().setup();
        } else {
            vars.getLogedInView().setup();
        }
        MyProjectManager.init(project, toolWindow);
        ProjectManager.getInstance().addProjectManagerListener(new MyProjectManagerListener()); //kautkaada servisa metode
    }

    private void initPlugin(Project project, ToolWindow toolWindow) {
        vars = new Vars();
        vars.init();
        vars.setSidePanel(this);
        WebDriver.init();

        stateParams = new StateParams();
        stateParams.setProject(project);
        stateParams.setToolWindow(toolWindow);
        stateParams.setSidePanel(this);
        stateParams.setVars(vars);
        StaticVars.addParams(stateParams);

        Runtime.getRuntime().addShutdownHook(new Thread(this::cleanup));
    }

    private void cleanup() {
        WebDriver.quit();
    }

    public StateParams getStateParams() {
        return stateParams;
    }
}
