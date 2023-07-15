package com.example.codewarsplugin.state.views;

import com.example.codewarsplugin.SidePanel;
import com.example.codewarsplugin.state.Panels;

import javax.swing.*;
import java.awt.*;

public class LoginView extends JPanel{


    private static JPanel sidePanel = Panels.getSidePanel();


    public static boolean setup() {
        sidePanel.setLayout(new BorderLayout());
        sidePanel.add(Panels.getTopImagePanel(), BorderLayout.NORTH);
        //sidePanel.add(Panels.getTitlePanel(), BorderLayout.CENTER);
        sidePanel.add(Panels.getLoginManager(), BorderLayout.SOUTH);
        return true;
    }

    public static void startSpinner() {
        Panels.getLoginManager().startSpinner();
        sidePanel.revalidate();
        sidePanel.repaint();
    }

    public static boolean cleanUp() {
        sidePanel.remove(Panels.getLoginManager());
        sidePanel.remove(Panels.getTopImagePanel());
        return false;
    }

    public static void stopSpinner(){
        Panels.getLoginManager().stopSpinner();
        sidePanel.revalidate();
        sidePanel.repaint();
    }
}
