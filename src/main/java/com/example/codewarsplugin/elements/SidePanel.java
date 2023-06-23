package com.example.codewarsplugin.elements;

import com.intellij.openapi.util.IconLoader;

import javax.swing.*;
import java.awt.*;

//177, 54, 30
public class SidePanel extends JPanel {

    public SidePanel() {

        setLayout(new BorderLayout());

        JPanel imageTopPanel = new JPanel();
        imageTopPanel.setLayout(new BorderLayout());

        Icon icon = IconLoader.getIcon("/icons/logo-square.png", SidePanel.class);
        JLabel imageLabel = new JLabel(icon);
        imageTopPanel.add(imageLabel, BorderLayout.CENTER);
        imageTopPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(imageTopPanel, BorderLayout.NORTH);


        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BorderLayout());
        JLabel titleLabel = new JLabel("sign in with your Codewars credentials");
        titleLabel.setFont(titleLabel.getFont().deriveFont(14f));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titlePanel.add(titleLabel, BorderLayout.NORTH);
        add(titlePanel, BorderLayout.CENTER);


        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridBagLayout());



        JLabel promptLabel = new JLabel("sign in with your Codewars credentials");
        promptLabel.setFont(titleLabel.getFont().deriveFont(10f));

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.insets = new Insets(5, 5, 5, 5);

        constraints.gridx = 0;
        constraints.gridy = 0;
        inputPanel.add(LoginManager.textField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        inputPanel.add(LoginManager.passwordField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 2;

        constraints.insets = new Insets(10, 5, 5, 5);
        inputPanel.add(LoginManager.submitButton, constraints);


        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.insets = new Insets(5, 5, 50, 5);
        inputPanel.add(promptLabel, constraints);

        add(inputPanel, BorderLayout.SOUTH);
    }
}
