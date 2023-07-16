package com.example.codewarsplugin.views;

import com.example.codewarsplugin.state.Vars;

import javax.swing.*;
import java.awt.*;

public class LoginView extends JPanel implements View{


    private Vars vars;

    public LoginView(Vars vars) {
        this.vars = vars;
    }

    public void setup() {
        vars.getSidePanel().add(vars.getTopImagePanel(), BorderLayout.NORTH);
        vars.getSidePanel().add(vars.getLoginManager(), BorderLayout.SOUTH);
        vars.getSidePanel().revalidate();
        vars.getSidePanel().repaint();
    }

    public void cleanup() {
        vars.getSidePanel().removeAll();
    }
}
