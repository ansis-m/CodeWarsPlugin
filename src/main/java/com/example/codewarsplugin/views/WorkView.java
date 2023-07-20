package com.example.codewarsplugin.views;

import com.example.codewarsplugin.SidePanel;
import com.example.codewarsplugin.components.NavigationBar;
import com.example.codewarsplugin.components.UserPanel;
import com.example.codewarsplugin.services.UserService;
import com.example.codewarsplugin.state.Vars;

import java.awt.*;

public class WorkView implements View{


    private Vars vars;
    private SidePanel sidePanel;
    private UserPanel userPanel;
    private NavigationBar navigationBar;

    public WorkView(Vars vars){
        this.vars = vars;
        this.sidePanel = vars.getSidePanel();
    }


    @Override
    public void setup() {
        userPanel = new UserPanel(UserService.getUser(), vars);
        navigationBar = new NavigationBar(vars);
        sidePanel.add(userPanel, BorderLayout.NORTH);
        sidePanel.add(navigationBar, BorderLayout.SOUTH);
        sidePanel.revalidate();
        sidePanel.repaint();
    }

    @Override
    public void cleanup() {
        sidePanel.removeAll();
        userPanel = null;
        navigationBar = null;
        sidePanel.revalidate();
        sidePanel.repaint();

    }
}
