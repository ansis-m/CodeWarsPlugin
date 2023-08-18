package com.example.codewarsplugin.components;

import com.example.codewarsplugin.SidePanel;
import com.example.codewarsplugin.models.kata.KataDirectory;
import com.example.codewarsplugin.services.files.create.FileManager;
import com.example.codewarsplugin.services.files.parse.KataDirectoryParser;
import com.example.codewarsplugin.state.Manager;
import com.example.codewarsplugin.state.Store;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.IconLoader;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

import static com.example.codewarsplugin.config.StringConstants.MESSAGE_ICON;

public class KataSelectorPanel extends JPanel {

    private final ComboBox<KataDirectory> directoryBox;
    private final DefaultComboBoxModel<KataDirectory> model;
    private final JButton selectorButton = new JButton("Setup Kata");
    private final JButton deleteButton = new JButton("Delete Kata");
    private final Store store;
    private final Manager manager;
    private final ArrayList<KataDirectory> kataDirectoryList;
    private final KataDirectoryParser parser;


    public KataSelectorPanel(Store store, Manager manager){
        super(new FlowLayout(FlowLayout.LEFT, 10, 0));
        this.store = store;
        parser = new KataDirectoryParser(store.getProject());
        this.manager = manager;
        kataDirectoryList = parser.getDirectoryList();
        removeCurrentDirectory();
        model = new DefaultComboBoxModel<>(kataDirectoryList.toArray(new KataDirectory[0]));
        directoryBox = new ComboBox<>(model);

        if(kataDirectoryList == null || kataDirectoryList.size() < 1) {
            return;
        }
        initComponents();
    }

    private void initComponents() {
        directoryBox.setRenderer(new KataDirectoryRenderer());

        selectorButton.setToolTipText("Setup a previously started kata from the currently opened project!");
        selectorButton.setIcon(AllIcons.Actions.Selectall);


        deleteButton.setToolTipText("Delete a previously started kata from the currently opened project!");
        deleteButton.setIcon(AllIcons.Actions.DeleteTagHover);


        add(directoryBox);
        add(selectorButton);
        add(deleteButton);
        addSelectorListeners();
    }

    private void addSelectorListeners() {
        selectorButton.addActionListener((event) -> {
            var directory = directoryBox.getSelectedItem();
            if (directory == null) {
                return;
            }
            manager.getShouldFetchAndCreateFilesOnUrlLoad().set(false);
            manager.loadKata((KataDirectory) directory, true);
        });

        deleteButton.addActionListener((event) -> {
            var directory = (KataDirectory) directoryBox.getSelectedItem();
            if (directory == null) {
                return;
            }
            int result = Messages.showOkCancelDialog("All \"" + directory.getRecord().getName() + "\" kata files will be deleted!", "Confirm Delete Kata Files", "Ok", "Cancel", IconLoader.getIcon(MESSAGE_ICON, SidePanel.class));
            if (result == 0) {
                new FileManager().deleteFiles(directory, store.getProject());
                manager.refresh();
            }
        });
    }

    private void removeCurrentDirectory() {
        kataDirectoryList.removeIf(directory -> directory.isTheSame(store.getDirectory()));
    }

    public int getCount(){
        return kataDirectoryList.size();
    }
}
