package com.example.codewarsplugin.state.views;

import com.example.codewarsplugin.SidePanel;
import com.example.codewarsplugin.state.Panels;


import javax.swing.*;
import java.awt.*;

public class LoginView extends View {

    public LoginView(SidePanel sidePanel) {
        super(sidePanel);
    }

    @Override
    public boolean setup() {
        sidePanel.setLayout(new BorderLayout());
        sidePanel.add(Panels.getTopImagePanel(), BorderLayout.NORTH);
        sidePanel.add(Panels.getTitlePanel(), BorderLayout.CENTER);
        sidePanel.add(Panels.getLoginManager(), BorderLayout.SOUTH);
        return true;
    }
    @Override
    public boolean cleanUp() {
        return true;
    }
}
