package com.example.codewarsplugin.state.views;

import com.example.codewarsplugin.SidePanel;
import com.example.codewarsplugin.state.Panels;

import java.awt.*;

public class LoginView extends View {
    public LoginView(SidePanel sidePanel) {
        super(sidePanel);
    }

    public static boolean setup() {
        sidePanel.setLayout(new BorderLayout());
        sidePanel.add(Panels.getTopImagePanel(), BorderLayout.NORTH);
        //sidePanel.add(Panels.getTitlePanel(), BorderLayout.CENTER);
        sidePanel.add(Panels.getLoginManager(), BorderLayout.SOUTH);
        return true;
    }

    public static boolean login() {
        Panels.getLoginManager().waitResponse();
        sidePanel.revalidate();
        sidePanel.repaint();
        return true;
    }

    public static boolean cleanUp() {
        sidePanel.remove(Panels.getLoginManager());
        sidePanel.remove(Panels.getTitlePanel());
        sidePanel.remove(Panels.getTopImagePanel());
        return true;
    }

    public static boolean getReady(){
        Panels.getLoginManager().getReady();
        sidePanel.revalidate();
        sidePanel.repaint();
        return true;
    }
}
