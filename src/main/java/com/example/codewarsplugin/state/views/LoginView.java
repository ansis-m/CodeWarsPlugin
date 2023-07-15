package com.example.codewarsplugin.state.views;

import com.example.codewarsplugin.SidePanel;
import com.example.codewarsplugin.state.Panels;

import javax.swing.*;
import java.awt.*;

public class LoginView extends JPanel{


    public static boolean setup() {

        System.out.println("\n\n\nSETUP\n\n\n");


        Panels.getSidePanel().setLayout(new BorderLayout());
        Panels.getSidePanel().add(Panels.getTopImagePanel(), BorderLayout.NORTH);
        //sidePanel.add(Panels.getTitlePanel(), BorderLayout.CENTER);
        Panels.getSidePanel().add(Panels.getLoginManager(), BorderLayout.SOUTH);
        Panels.getSidePanel().revalidate();
        Panels.getSidePanel().repaint();
        return true;
    }

    public static void startSpinner() {
        Panels.getLoginManager().startSpinner();
        Panels.getSidePanel().revalidate();
        Panels.getSidePanel().repaint();
    }

    public static boolean cleanUp() {
        Panels.getSidePanel().remove(Panels.getLoginManager());
        Panels.getSidePanel().remove(Panels.getTopImagePanel());
        return false;
    }

    public static void stopSpinner(){
        Panels.getLoginManager().stopSpinner();
        Panels.getSidePanel().revalidate();
        Panels.getSidePanel().repaint();
    }
}
