package com.example.codewarsplugin.components;

import com.intellij.ui.AnimatedIcon;

import javax.swing.*;
import java.awt.*;

public class OverlaySpinner extends JPanel {

    public OverlaySpinner(){
        super(new GridBagLayout());
//        setOpaque(false);
        add(new JLabel(new AnimatedIcon.Big()));
    }

//    @Override
//    protected void paintComponent(Graphics g) {
//        super.paintComponent(g);
//        g.setColor(new Color(0, 0, 0, 150));
//        g.fillRect(0, 0, getWidth(), getHeight());
//    }
}
