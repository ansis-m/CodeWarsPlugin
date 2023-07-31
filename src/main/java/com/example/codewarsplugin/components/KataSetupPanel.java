package com.example.codewarsplugin.components;

import com.example.codewarsplugin.models.kata.KataDirectory;
import com.example.codewarsplugin.services.files.parse.KataDirectoryParser;
import com.example.codewarsplugin.state.Store;
import com.intellij.openapi.ui.ComboBox;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class KataSetupPanel extends JPanel {

    private Store store;

    public KataSetupPanel(Store store) {
        super();
        this.store = store;
        setLayout(new GridBagLayout());
        addElementsToPanel();
    }

    private void addElementsToPanel() {

        var constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.insets = new Insets(5, 5, 5, 5);

        constraints.gridx = 0;
        constraints.gridy = 0;

        var label = new JLabel("Select Kata from the current project");
        label.setFont(label.getFont().deriveFont(14f));
        add(label, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;

        add(new KataSelectorPanel(store), constraints);

        constraints.gridx = 0;
        constraints.gridy = 2;

        var label2 = new JLabel("OR");
        label2.setFont(label.getFont().deriveFont(14f));
        add(label2, constraints);

        constraints.gridx = 0;
        constraints.gridy = 3;
        add(new KataRecordPanel(store), constraints);
    }
}
