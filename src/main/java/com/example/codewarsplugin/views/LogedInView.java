package com.example.codewarsplugin.views;

import com.example.codewarsplugin.SidePanel;
import com.example.codewarsplugin.components.UserPanel;
import com.example.codewarsplugin.services.UserService;
import com.example.codewarsplugin.state.Store;

import javax.swing.*;
import java.awt.*;

public class LogedInView implements View{
    private JPanel userPanel;
    private Store store;
    private SidePanel sidePanel;

    public LogedInView(Store store) {
        super();
        this.store = store;
        this.sidePanel = store.getSidePanel();
    }

    public void setup() {
        userPanel = new UserPanel(UserService.getUser(), store);
        sidePanel.add(userPanel, BorderLayout.NORTH);
        sidePanel.add(store.getKataPrompt(), BorderLayout.CENTER);
        sidePanel.revalidate();
        sidePanel.repaint();
    }

    public void cleanup(){
        sidePanel.removeAll();
        userPanel = null;
    }
}
