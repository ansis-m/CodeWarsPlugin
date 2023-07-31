package com.example.codewarsplugin.views;

import com.example.codewarsplugin.SidePanel;
import com.example.codewarsplugin.components.KataSetupPanel;
import com.example.codewarsplugin.components.UserPanel;
import com.example.codewarsplugin.services.UserService;
import com.example.codewarsplugin.services.files.parse.KataDirectoryParser;
import com.example.codewarsplugin.state.Store;

import javax.swing.*;
import java.awt.*;

public class LogedInView implements View{
    private JPanel userPanel;
    private final Store store;
    private final SidePanel sidePanel;

    public LogedInView(Store store) {
        super();
        this.store = store;
        this.sidePanel = store.getSidePanel();
    }

    public void setup() {
        userPanel = new UserPanel(UserService.getUser(), store);
        sidePanel.add(userPanel, BorderLayout.NORTH);

        if ( KataDirectoryParser.getDirectoryList().size() > 0) {
            sidePanel.add(new KataSetupPanel(store), BorderLayout.CENTER);
        }

        sidePanel.revalidate();
        sidePanel.repaint();
    }

    public void cleanup(){
        sidePanel.removeAll();
        userPanel = null;
    }

    @Override
    public void refreshUserPanel() {
        sidePanel.remove(userPanel);
        userPanel = new UserPanel(UserService.getUser(), store);
        sidePanel.add(userPanel, BorderLayout.NORTH);
    }
}
