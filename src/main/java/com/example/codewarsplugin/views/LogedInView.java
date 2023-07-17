package com.example.codewarsplugin.views;

import com.example.codewarsplugin.SidePanel;
import com.example.codewarsplugin.components.UserPanel;
import com.example.codewarsplugin.services.UserService;
import com.example.codewarsplugin.state.Vars;

import javax.swing.*;
import java.awt.*;

public class LogedInView implements View{
    private JPanel userPanel;
    private Vars vars;
    private SidePanel sidePanel;

    public LogedInView(Vars vars) {
        super();
        this.vars = vars;
        this.sidePanel = vars.getSidePanel();
    }

    public void setup() {
        userPanel = new UserPanel(UserService.getUser(), vars);
        sidePanel.add(userPanel, BorderLayout.NORTH);
        sidePanel.add(vars.getKataPrompt(), BorderLayout.CENTER);
        sidePanel.revalidate();
        sidePanel.repaint();
    }

    public void cleanup(){
        sidePanel.removeAll();
        userPanel = null;
    }
}
