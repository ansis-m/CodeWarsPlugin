package com.example.codewarsplugin.components;

import com.example.codewarsplugin.SidePanel;
import com.intellij.openapi.util.IconLoader;

import javax.swing.*;
import java.awt.*;

public class TopImagePanel extends JPanel {

    private static Icon icon;

    public TopImagePanel(){
        super();
        icon = IconLoader.getIcon("/icons/logo-square.png", SidePanel.class);
        setLayout(new BorderLayout());
        JLabel imageLabel = new JLabel(icon);
        add(imageLabel, BorderLayout.CENTER);
        setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
    }
}
