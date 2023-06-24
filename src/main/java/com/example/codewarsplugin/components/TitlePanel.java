package com.example.codewarsplugin.components;

import javax.swing.*;
import java.awt.*;

public class TitlePanel extends JPanel {

    public TitlePanel(){
        super();

        setLayout(new BorderLayout());
        JLabel titleLabel = new JLabel("retrieve Codewars \u2615 katas and submit solutions from Intellij");
        titleLabel.setFont(titleLabel.getFont().deriveFont(14f));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(titleLabel, BorderLayout.NORTH);
    }
}
