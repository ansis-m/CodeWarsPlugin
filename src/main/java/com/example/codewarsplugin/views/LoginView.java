package com.example.codewarsplugin.views;

import com.example.codewarsplugin.SidePanel;
import com.example.codewarsplugin.components.LoginPanel;
import com.example.codewarsplugin.components.TopImagePanel;
import com.example.codewarsplugin.state.Store;

import java.awt.*;

public class LoginView implements View{

    private Store store;
    private SidePanel sidePanel;
    private LoginPanel loginPanel;


    public LoginView(Store store) {
        this.store = store;
        this.sidePanel = store.getSidePanel();
    }

    public void setup() {
        loginPanel = new LoginPanel();
        store.setLoginPanel(loginPanel);
        sidePanel.add(new TopImagePanel(), BorderLayout.NORTH);
        sidePanel.add(loginPanel, BorderLayout.SOUTH);
        sidePanel.revalidate();
        sidePanel.repaint();
    }

    public void cleanup() {
        loginPanel.stopSpinner();
        sidePanel.removeAll();
    }

    @Override
    public void refreshUserPanel() {

    }
}
