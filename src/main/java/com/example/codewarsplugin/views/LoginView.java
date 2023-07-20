package com.example.codewarsplugin.views;

import com.example.codewarsplugin.SidePanel;
import com.example.codewarsplugin.state.Store;

import java.awt.*;

public class LoginView implements View{

    private Store store;
    private SidePanel sidePanel;

    public LoginView(Store store) {
        this.store = store;
        this.sidePanel = store.getSidePanel();
    }

    public void setup() {

        sidePanel.add(store.getTopImagePanel(), BorderLayout.NORTH);
        sidePanel.add(store.getLoginManager(), BorderLayout.SOUTH);
        sidePanel.revalidate();
        sidePanel.repaint();
    }

    public void cleanup() {
        store.getLoginManager().stopSpinner();
        sidePanel.removeAll();
    }
}
