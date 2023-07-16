package com.example.codewarsplugin.views;

import com.example.codewarsplugin.state.Vars;

import javax.swing.*;
import java.awt.*;

public class LoginView extends JPanel{


    private Vars vars;

    public LoginView(Vars vars) {
        this.vars = vars;
    }

    public boolean setup() {
        vars.getSidePanel().add(vars.getTopImagePanel(), BorderLayout.NORTH);
        vars.getSidePanel().add(vars.getLoginManager(), BorderLayout.SOUTH);
        vars.getSidePanel().revalidate();
        vars.getSidePanel().repaint();
        return true;
    }


    public boolean cleanUp() {
        vars.getSidePanel().remove(vars.getLoginManager());
        vars.getSidePanel().remove(vars.getTopImagePanel());
        return false;
    }

}
