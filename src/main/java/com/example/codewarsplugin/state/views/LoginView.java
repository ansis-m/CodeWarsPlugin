package com.example.codewarsplugin.state.views;

import com.example.codewarsplugin.SidePanel;
import com.example.codewarsplugin.state.Panels;

import javax.swing.*;
import java.awt.*;

public class LoginView extends JPanel{


    private Panels panels;

    public LoginView(Panels panels) {
        this.panels = panels;
    }

    public boolean setup() {

        System.out.println("\n\n\nSETUP\n\n\n");


        panels.getSidePanel().setLayout(new BorderLayout());
        panels.getSidePanel().add(panels.getTopImagePanel(), BorderLayout.NORTH);
        panels.getSidePanel().add(panels.getLoginManager(), BorderLayout.SOUTH);
        panels.getSidePanel().revalidate();
        panels.getSidePanel().repaint();
        return true;
    }


    public boolean cleanUp() {
        panels.getSidePanel().remove(panels.getLoginManager());
        panels.getSidePanel().remove(panels.getTopImagePanel());
        return false;
    }

    public void stopSpinner(){
        panels.getLoginManager().stopSpinner();
        panels.getSidePanel().revalidate();
        panels.getSidePanel().repaint();
    }
}
