package com.example.codewarsplugin.components;

import com.example.codewarsplugin.models.kata.KataDirectory;
import com.example.codewarsplugin.state.Store;

import javax.swing.*;

import java.io.IOException;

public class DescriptionPanel extends JPanel {

    private final Store store;
    private final KataDirectory directory;

    public DescriptionPanel(Store store) {
        this.store = store;
        directory = store.getDirectory();

    }

}
