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
    private JPanel selectorPanel = new JPanel();
    private JButton selectorButton = new JButton("Setup Kata");
    public KataSelectorPanel(Store store) {
        super();
        this.store = store;
        this.kataDirectoryList = KataDirectoryParser.getDirectoryList();
        setLayout(new GridBagLayout());
        addElementsToPanel();
        addSelectorListeners();
    }

    private void addSelectorListeners() {

        selectorButton.addActionListener((event) -> {
            store.setCurrentKataDirectory((KataDirectory) directoryBox.getSelectedItem());
            store.getCurrentView().cleanup();
            store.getPreviousViews().add(store.getCurrentView());
            store.getWorkView().setup();
            store.setCurrentView(store.getWorkView());

        });


    }

    private void addElementsToPanel() {

        var constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.insets = new Insets(0, 5, 5, 5);

        constraints.gridx = 0;
        constraints.gridy = 0;

        var label = new JLabel("Select Kata from the current project");
        label.setFont(label.getFont().deriveFont(14f));
        add(label, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;

        selectorPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));
        directoryBox = new ComboBox<>(kataDirectoryList.toArray());
        selectorPanel.add(directoryBox);
        selectorPanel.add(selectorButton);
        add(selectorPanel, constraints);

        constraints.gridx = 0;
        constraints.gridy = 2;

        var label2 = new JLabel("OR");
        label2.setFont(label.getFont().deriveFont(14f));
        add(label2, constraints);
    }
}
