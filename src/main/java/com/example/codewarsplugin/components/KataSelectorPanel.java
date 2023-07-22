package com.example.codewarsplugin.components;

import com.example.codewarsplugin.models.kata.KataDirectory;
import com.example.codewarsplugin.services.files.parse.KataDirectoryParser;
import com.example.codewarsplugin.state.Store;
import com.intellij.openapi.ui.ComboBox;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class KataSelectorPanel extends JPanel {

    private Store store;
    private ArrayList<KataDirectory> kataDirectoryList;
    private ComboBox<Object> directoryBox;
    public KataSelectorPanel(Store store) {
        super();
        this.store = store;
        this.kataDirectoryList = KataDirectoryParser.getDirectoryList();
        setLayout(new GridBagLayout());
        addElementsToPanel();
        addSelectorListeners();
    }

    private void addSelectorListeners() {
    }

    private void addElementsToPanel() {

        var constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.insets = new Insets(5, 5, 5, 5);

        constraints.gridx = 0;
        constraints.gridy = 0;

        var label = new JLabel("");
        label.setFont(label.getFont().deriveFont(14f));
        add(label, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;

        directoryBox = new ComboBox<>(kataDirectoryList.toArray());
        add(directoryBox, constraints);



    }
}
