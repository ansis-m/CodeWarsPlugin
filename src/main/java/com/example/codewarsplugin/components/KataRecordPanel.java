package com.example.codewarsplugin.components;

import com.example.codewarsplugin.models.kata.KataInput;
import com.example.codewarsplugin.models.kata.KataRecord;
import com.example.codewarsplugin.services.katas.KataInputService;
import com.example.codewarsplugin.services.katas.KataInputServiceClient;
import com.example.codewarsplugin.state.Vars;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.AnimatedIcon;

import javax.swing.*;
import java.awt.*;

public class KataRecordPanel extends JPanel implements KataInputServiceClient {

    private KataRecord record;
    private Vars vars;
    private JButton button = new JButton("Setup Kata");
    private ComboBox<String> languageBox;
    private JPanel cardButtonPanel = new JPanel();
    private CardLayout cardButtonLayout = new CardLayout();
    private JLabel spinner = new JLabel(new AnimatedIcon.Big());

    public KataRecordPanel(KataRecord record, Vars vars){
        super();
        this.record = record;
        this.vars = vars;
        setLayout(new GridBagLayout());
        setupButtonListeners();
        setupCardPanel();
        addComponents();
    }

    private void setupCardPanel() {
        cardButtonPanel.setLayout(cardButtonLayout);
        cardButtonPanel.add(button, "button");
        cardButtonPanel.add(spinner, "spinner");
    }

    private void addComponents() {
        var constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.insets = new Insets(5, 5, 5, 5);

        constraints.gridx = 0;
        constraints.gridy = 0;

        var label = new JLabel(record == null ? "Selected Kata: " : "Selected Kata: " + record.getName());
        label.setFont(label.getFont().deriveFont(14f));
        add(label, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;

        languageBox = new ComboBox<>(record == null ? new String[]{} : record.getLanguages());
        add(languageBox, constraints);


        constraints.gridx = 0;
        constraints.gridy = 2;
        add(cardButtonPanel, constraints);
    }

    private void setupButtonListeners() {

        button.addActionListener((e) -> {
            String language = (String) languageBox.getSelectedItem();
            record.setSelectedLanguage(language);
            KataInputService.getKata(record, this);
        });

    }

    @Override
    public void processKataInput(KataInput kataInput) {
        System.out.println("Kata input received: " + kataInput.toString());
    }

    @Override
    public void processInputNotFound(Exception e) {
        try {
            throw e;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void startSpinner() {
        cardButtonLayout.show(cardButtonPanel, "spinner");
        revalidate();
        repaint();

    }

    @Override
    public void stopSpinner() {
        cardButtonLayout.show(cardButtonPanel, "button");
        revalidate();
        repaint();
    }
}
