package com.example.codewarsplugin.views;

import com.example.codewarsplugin.SidePanel;
import com.example.codewarsplugin.components.DescriptionPanel;
import com.example.codewarsplugin.components.NavigationBar;
import com.example.codewarsplugin.components.UserPanel;
import com.example.codewarsplugin.models.kata.KataRecord;
import com.example.codewarsplugin.services.UserService;
import com.example.codewarsplugin.state.Store;
import com.intellij.ui.components.JBScrollPane;


import javax.swing.*;
import java.awt.*;

import static com.example.codewarsplugin.state.Store.borwser;

public class DescriptionView implements View{


    private final Store store;
    private final SidePanel sidePanel;
    private UserPanel userPanel;
    private NavigationBar navigationBar;

    private DescriptionPanel descriptionPanel;

    public DescriptionView(Store store) {
        super();
        this.store = store;
        this.sidePanel = store.getSidePanel();
    }


    @Override
    public void setup() {


        var record = store.getDirectory().getRecord();


        userPanel = new UserPanel(UserService.getUser(), store);
        sidePanel.add(userPanel, BorderLayout.NORTH);


        borwser.loadURL(record.getUrl() + "/" + record.getSelectedLanguage().toLowerCase());

        sidePanel.add(borwser.getComponent(), BorderLayout.CENTER);


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
