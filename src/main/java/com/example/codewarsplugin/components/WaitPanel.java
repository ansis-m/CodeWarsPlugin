package com.example.codewarsplugin.components;

import com.intellij.ui.AnimatedIcon;

import javax.swing.*;
import java.awt.*;

public class WaitPanel extends JPanel {

    private JComponent progressComponent;
    public WaitPanel(){
        super();
        ImageIcon imgIcon = new ImageIcon(this.getClass().getResource("/icons/giphy.gif"));





        JLabel label = new JLabel(new AnimatedIcon.Big());
        add(label, BorderLayout.NORTH);
    }
}
