package com.example.codewarsplugin.state.views;

import com.example.codewarsplugin.SidePanel;
import com.example.codewarsplugin.state.Panels;

import java.awt.*;

public class LoginView extends View {


    private static boolean ready = false;
    public LoginView(SidePanel sidePanel) {
        super(sidePanel);
    }

    public static boolean setup() {
        sidePanel.setLayout(new BorderLayout());
        sidePanel.add(Panels.getTopImagePanel(), BorderLayout.NORTH);
        sidePanel.add(Panels.getTitlePanel(), BorderLayout.CENTER);
        sidePanel.add(Panels.getLoginManager(), BorderLayout.SOUTH);
        ready = true;
        return true;
    }

    public static boolean cleanUp() {
        if (!ready)
            return false;
        sidePanel.remove(Panels.getLoginManager());
        sidePanel.revalidate();
        sidePanel.repaint();
        ready = false;
        return true;
    }

    public static boolean getReady(){
        if(ready) {
            return false;
        }
        sidePanel.add(Panels.getLoginManager(), BorderLayout.SOUTH);
        sidePanel.revalidate();
        sidePanel.repaint();
        ready = true;
        return true;
    }
}
