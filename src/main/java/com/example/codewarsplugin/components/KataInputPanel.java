package com.example.codewarsplugin.components;

import com.example.codewarsplugin.models.kata.KataInput;
import com.example.codewarsplugin.models.kata.KataRecord;
import com.example.codewarsplugin.services.files.FileService;
import com.example.codewarsplugin.services.files.FileServiceClient;
import com.example.codewarsplugin.services.katas.KataInputService;
import com.example.codewarsplugin.services.katas.KataInputServiceClient;
import com.example.codewarsplugin.state.Vars;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.AnimatedIcon;
import com.intellij.ui.JBColor;

import javax.swing.*;
import java.awt.*;

public class KataInputPanel extends JPanel implements KataInputServiceClient, FileServiceClient {

    private KataRecord record;
    private Vars vars;
    private JButton button = new JButton("Setup Kata");
    private ComboBox<String> languageBox;
    private JPanel cardButtonPanel = new JPanel();
    private CardLayout cardButtonLayout = new CardLayout();
    private JLabel spinner = new JLabel(new AnimatedIcon.Big());
    private JLabel failMessage = new JLabel("\u00A0");

    public KataInputPanel(KataRecord record, Vars vars){
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

        constraints.gridx = 0;
        constraints.gridy = 3;
        add(failMessage, constraints);
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
        FileService.createFile(kataInput, record, this);
    }

    @Override
    public void processInputNotFound(Exception exception) {

        failMessage.setText("Setup failed: " + exception.getMessage());
        failMessage.setForeground(JBColor.RED);
        revalidate();
        repaint();
        Timer timer = new Timer(2500, e -> SwingUtilities.invokeLater(() -> {
            failMessage.setText("\u00A0");
            revalidate();
            repaint();
        }));
        timer.setRepeats(false);
        timer.start();
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

    @Override
    public void notifyFileExists() {

        failMessage.setText("This kata has been configures before in this project.");
        failMessage.setForeground(JBColor.RED);
        revalidate();
        repaint();
        Timer timer = new Timer(2500, e -> SwingUtilities.invokeLater(() -> {
            failMessage.setText("\u00A0");
            revalidate();
            repaint();
        }));
        timer.setRepeats(false);
        timer.start();
    }

    @Override
    public void transitionToWorkView() {
        System.out.println("\n\nTransition to work view!\n\n");
    }

    @Override
    public void notifyFileCreationFailed() {

    }
}
