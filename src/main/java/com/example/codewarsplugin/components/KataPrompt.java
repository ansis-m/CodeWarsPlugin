package com.example.codewarsplugin.components;

import javax.swing.*;
import java.awt.*;

public class KataPrompt extends JPanel {


    public static JTextField textField;
    public static JButton submitButton;
    public static JLabel promptLabel;
    public static JLabel invalidKataLabel;
    private static final GridBagConstraints constraints = new GridBagConstraints();


    public KataPrompt(){
        super();
        setLayout(new GridBagLayout());
        textField = new JTextField(30);
        submitButton = new JButton("Get Kata");
        promptLabel = new JLabel("Paste kata title or code!");
        invalidKataLabel = new JLabel("\u202F");
        invalidKataLabel.setForeground(Color.red);
        invalidKataLabel.setFont(promptLabel.getFont().deriveFont(20f));
        //addEnterKeyListener();
        //addButtonPushedListener();
        addElementsToPanel();
    }

    private void addElementsToPanel() {

        promptLabel.setFont(promptLabel.getFont().deriveFont(14f));

        constraints.anchor = GridBagConstraints.CENTER;
        constraints.insets = new Insets(5, 5, 5, 5);

        constraints.gridx = 0;
        constraints.gridy = 0;
        add(textField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;

        constraints.insets = new Insets(0, 5, 5, 5);
        add(promptLabel, constraints);

        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.insets = new Insets(5, 5, 50, 5);
        add(submitButton, constraints);

        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.insets = new Insets(5, 5, 5, 5);
        add(invalidKataLabel, constraints);

    }
}
