package com.example.codewarsplugin.components;

import com.example.codewarsplugin.SidePanel;
import com.example.codewarsplugin.state.Vars;
import com.intellij.openapi.util.IconLoader;

import javax.swing.*;
import java.awt.*;

public class TopImagePanel extends JPanel {

    private Icon icon = IconLoader.getIcon("/icons/logo-square.png", SidePanel.class);

    public TopImagePanel(Vars vars){
        super();
        setLayout(new BorderLayout());
        JLabel imageLabel = new JLabel(icon);
        add(imageLabel, BorderLayout.CENTER);
        setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
    }
}
