package com.example.codewarsplugin.views;

import com.example.codewarsplugin.SidePanel;
import com.example.codewarsplugin.state.Vars;

import java.awt.*;

public class LoginView implements View{

    private Vars vars;
    private SidePanel sidePanel;

    public LoginView(Vars vars) {
        this.vars = vars;
        this.sidePanel = vars.getSidePanel();
    }

    public void setup() {

        sidePanel.add(vars.getTopImagePanel(), BorderLayout.NORTH);
        sidePanel.add(vars.getLoginManager(), BorderLayout.SOUTH);
        sidePanel.revalidate();
        sidePanel.repaint();
    }

    public void cleanup() {
        vars.getLoginManager().stopSpinner();
        sidePanel.removeAll();
    }
}
