package com.example.codewarsplugin.components;

import com.example.codewarsplugin.models.kata.KataDirectory;
import com.example.codewarsplugin.state.Store;
import com.example.codewarsplugin.state.TabManager;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.ui.ComboBox;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class KataSelectorPanel extends JPanel {

    private final ComboBox<Object> directoryBox;
    private final JButton selectorButton = new JButton("Setup Kata");
    private final Store store;
    private final TabManager manager;
    private final ArrayList<KataDirectory> kataDirectoryList;

    public KataSelectorPanel(Store store, TabManager tabManager){
        this.store = store;
        setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
        this.kataDirectoryList = store.getDirectories();
        directoryBox = new ComboBox<>(kataDirectoryList.toArray());
        this.manager = tabManager;
        add(directoryBox);
        add(selectorButton);
        addSelectorListeners();
    }

    private void addSelectorListeners() {
        selectorButton.addActionListener((event) -> {
            var directory = directoryBox.getSelectedItem();
            manager.getShouldFetchAndCreateFilesOnUrlLoad().set(false);
            manager.loadWorkspaceTab((KataDirectory) directory, true);
        });
    }
}
