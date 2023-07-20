package com.example.codewarsplugin.views;

import com.example.codewarsplugin.SidePanel;
import com.example.codewarsplugin.components.NavigationBar;
import com.example.codewarsplugin.components.UserPanel;
import com.example.codewarsplugin.models.kata.KataInput;
import com.example.codewarsplugin.models.kata.KataRecord;
import com.example.codewarsplugin.services.UserService;
import com.example.codewarsplugin.state.Store;

import java.awt.*;

public class WorkView implements View{


    private Store store;
    private SidePanel sidePanel;
    private UserPanel userPanel;
    private NavigationBar navigationBar;

    private KataInput input;
    private KataRecord record;

    public WorkView(Store store){
        this.store = store;
        this.sidePanel = store.getSidePanel();
    }


    @Override
    public void setup() {
        userPanel = new UserPanel(UserService.getUser(), store);
        navigationBar = new NavigationBar(store);
        sidePanel.add(userPanel, BorderLayout.NORTH);
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
}
