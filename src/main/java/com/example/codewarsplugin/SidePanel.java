package com.example.codewarsplugin;

import com.example.codewarsplugin.state.Store;
import com.intellij.openapi.project.Project;

import javax.swing.*;
import java.awt.*;

public class SidePanel extends JPanel {


    private final CardLayout cardLayout = new CardLayout();

    public SidePanel(Project project) {
        super();
        setLayout(cardLayout);
        new Store(this, project);
    }

    public CardLayout getCardLayout() {
        return cardLayout;
    }
}
