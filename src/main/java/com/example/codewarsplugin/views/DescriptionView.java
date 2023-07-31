package com.example.codewarsplugin.views;

import com.example.codewarsplugin.SidePanel;
import com.example.codewarsplugin.components.NavigationBar;
import com.example.codewarsplugin.components.UserPanel;
import com.example.codewarsplugin.services.UserService;
import com.example.codewarsplugin.state.Store;


import java.awt.*;

public class DescriptionView implements View{


    private final Store store;
    private final SidePanel sidePanel;
    private UserPanel userPanel;
    private NavigationBar navigationBar;

    public DescriptionView(Store store) {
        super();
        this.store = store;
        this.sidePanel = store.getSidePanel();
    }


    @Override
    public void setup() {
        userPanel = new UserPanel(UserService.getUser(), store);
        sidePanel.add(userPanel, BorderLayout.NORTH);

        navigationBar = new NavigationBar(store);
        sidePanel.add(navigationBar, BorderLayout.SOUTH);
        sidePanel.revalidate();
        sidePanel.repaint();

    }

    @Override
    public void cleanup() {
        sidePanel.removeAll();
        userPanel = null;
        navigationBar = null;
        sidePanel.revalidate();
        sidePanel.repaint();
    }

    @Override
    public void refreshUserPanel() {

    }
}
