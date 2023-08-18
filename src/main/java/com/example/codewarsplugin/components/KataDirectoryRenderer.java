package com.example.codewarsplugin.components;

import com.example.codewarsplugin.models.kata.KataDirectory;
import com.intellij.icons.AllIcons;

import javax.swing.*;
import java.awt.*;

public class KataDirectoryRenderer extends JLabel implements javax.swing.ListCellRenderer<KataDirectory> {

    public KataDirectoryRenderer() {
        setOpaque(true);
        setHorizontalTextPosition(JLabel.LEFT);
    }

    public KataDirectoryRenderer(KataDirectory directory) {
        this();
        setupWithDirectory(directory);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends KataDirectory> list, KataDirectory directory, int index, boolean isSelected, boolean cellHasFocus) {

        setupWithDirectory(directory);
        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }
        setFont(list.getFont());
        return this;
    }

    public void setupWithDirectory(KataDirectory directory) {
        if (directory != null) {
            setText(directory.toString());
            if (directory.getRecord().isCompleted()) {
                setIcon(AllIcons.Actions.Commit);
            } else {
                setIcon(null);
            }
        } else {
            setText("");
            setIcon(null);
        }
    }
}
