package com.example.codewarsplugin.components;

import com.example.codewarsplugin.models.kata.KataDirectory;
import com.example.codewarsplugin.services.files.parse.KataDirectoryParser;
import com.example.codewarsplugin.state.Store;
import com.intellij.openapi.ui.ComboBox;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class KataSelectorPanel extends JPanel {

    private ComboBox<Object> directoryBox;
    private JButton selectorButton = new JButton("Setup Kata");
    private Store store;
    private ArrayList<KataDirectory> kataDirectoryList;

    public KataSelectorPanel(Store store){
        this.store = store;
        setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        this.kataDirectoryList = KataDirectoryParser.getDirectoryList();
        directoryBox = new ComboBox<>(kataDirectoryList.toArray());
        add(directoryBox);
        add(selectorButton);
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
}
