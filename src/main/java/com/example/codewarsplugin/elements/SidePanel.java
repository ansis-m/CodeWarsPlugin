package com.example.codewarsplugin.elements;

import javax.swing.*;
import java.awt.*;

public class SidePanel extends JPanel {

    public SidePanel() {
        // Set the layout manager for the panel
        setLayout(new FlowLayout());

        // Create the text inputs
        JTextField textField1 = new JTextField(10);
        JTextField textField2 = new JTextField(10);

        // Create the submit button
        JButton submitButton = new JButton("Submit");

        // Add the components to the panel
        add(textField1);
        add(textField2);
        add(submitButton);
    }
}
