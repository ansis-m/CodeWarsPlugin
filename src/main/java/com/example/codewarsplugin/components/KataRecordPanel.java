package com.example.codewarsplugin.components;

import com.example.codewarsplugin.models.kata.KataRecord;
import com.example.codewarsplugin.state.Vars;
import com.intellij.openapi.ui.ComboBox;

import javax.swing.*;
import java.awt.*;

public class KataRecordPanel extends JPanel {

    private KataRecord record;
    private Vars vars;

    public KataRecordPanel(KataRecord record, Vars vars){
        super();
        this.record = record;
        this.vars = vars;

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());

        var constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.insets = new Insets(5, 5, 5, 5);

        constraints.gridx = 0;
        constraints.gridy = 0;

        var label = new JLabel(record == null ? "Selected Kata: " : "Selected Kata: " + record.getName());
        label.setFont(label.getFont().deriveFont(14f));
        panel.add(label, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;

        ComboBox<String> languageBox = new ComboBox<>(record == null ? new String[]{} : record.getLanguages());
        panel.add(languageBox, constraints);


        var button = new JButton("Setup Kata");
        constraints.gridx = 0;
        constraints.gridy = 2;
        panel.add(button, constraints);
    }


}
