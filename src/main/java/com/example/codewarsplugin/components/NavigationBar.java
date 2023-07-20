package com.example.codewarsplugin.components;

import com.example.codewarsplugin.state.Store;

import javax.swing.*;
import java.awt.*;

public class NavigationBar extends JPanel {

    private Store store;
    private JButton backButton = new JButton("BACK");

    public NavigationBar(Store store) {
        super();
        this.store = store;
        setLayout(new FlowLayout(FlowLayout.LEFT, 20, 20));
        addListener();
        add(backButton);
    }

    private void addListener() {
        backButton.addActionListener((e) -> {
            var previousView = store.getPreviousView();

            store.getCurrentView().cleanup();
            store.getPreviousViews().add(store.getCurrentView());

            previousView.setup();
            store.setCurrentView(previousView);

            store.getSidePanel().revalidate();
            store.getSidePanel().repaint();
        });
    }
}
