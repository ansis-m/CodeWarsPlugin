package com.example.codewarsplugin.components;

import javax.swing.*;
import java.awt.*;

public class WaitPanel extends JPanel {

    private JComponent progressComponent;
    public WaitPanel(){
        super();
        ImageIcon imgIcon = new ImageIcon(this.getClass().getResource("/icons/giphy.gif"));
        JLabel label = new JLabel(imgIcon);
        add(label, BorderLayout.NORTH);
    }
}
