package com.example.codewarsplugin.components;

import com.example.codewarsplugin.models.kata.KataRecord;
import com.example.codewarsplugin.services.katas.KataIdService;
import com.example.codewarsplugin.state.Vars;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.AnimatedIcon;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KataPrompt extends JPanel {

    private JTextField textField;
    private JButton submitButton;
    private JLabel promptLabel;
    private JLabel invalidKataLabel;
    private JPanel cardButtonPanel;
    private CardLayout cardButtonLayout;
    private JPanel cardKataPanel;
    private CardLayout cardKataLayout;
    private JPanel emptyKataPanel;
    private JPanel filledKataPanel;
    private JLabel spinner;
    private GridBagConstraints constraints;
    private KataRecord record;
    private Vars vars;
    private KataIdService kataIdService = new KataIdService();


    public KataPrompt(Vars vars){
        super();
        this.vars = vars;
        cardButtonPanel = new JPanel();
        cardButtonLayout = new CardLayout();
        cardKataPanel = new JPanel();
        cardKataLayout = new CardLayout();
        emptyKataPanel = new JPanel();
        filledKataPanel = getFilledKataPanel(null);
        spinner = new JLabel(new AnimatedIcon.Big());
        constraints = new GridBagConstraints();

        setLayout(new GridBagLayout());
        textField = new JTextField(30);
        submitButton = new JButton("Get Kata");
        promptLabel = new JLabel("Paste kata title!");
        invalidKataLabel = new JLabel("Kata not found!");
        invalidKataLabel.setForeground(Color.red);
        invalidKataLabel.setFont(promptLabel.getFont().deriveFont(15f));
        cardButtonPanel.setLayout(cardButtonLayout);
        cardKataPanel.setLayout(cardKataLayout);
        //addEnterKeyListener();
        addElementsToPanel();
        addKataRecordSearchListeners();
    }

    private JPanel getFilledKataPanel(KataRecord record) {

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
        return panel;
    }

    private void addKataRecordSearchListeners() {
        submitButton.addActionListener(e -> {
            startSpinner();
            kataIdService.getKataRecord(textField.getText(), vars);
        });
        textField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    startSpinner();
                    kataIdService.getKataRecord(textField.getText(), vars);
                }
            }
            @Override
            public void keyReleased(KeyEvent e) {}
        });
    }

    public void completeCallback(KataRecord record){
        System.out.println("completeCallback: " + record);
        this.record = record;
        stopSpinner();
        if (record == null) {
            cardKataLayout.show(cardKataPanel, "invalid");
            vars.getSidePanel().revalidate();
            vars.getSidePanel().repaint();
            Timer timer = new Timer(2000, e -> SwingUtilities.invokeLater(() -> {
                cardKataLayout.show(cardKataPanel, "empty");
                vars.getSidePanel().revalidate();
                vars.getSidePanel().repaint();
            }));
            timer.setRepeats(false);
            timer.start();
        } else {
            cardKataPanel.remove(filledKataPanel);
            filledKataPanel = getFilledKataPanel(record);
            cardKataPanel.add(filledKataPanel, "kata");
            cardKataLayout.show(cardKataPanel, "kata");
            cardKataPanel.revalidate();
            cardKataPanel.repaint();
        }
    }

    private void addElementsToPanel() {

        promptLabel.setFont(promptLabel.getFont().deriveFont(14f));

        constraints.anchor = GridBagConstraints.CENTER;
        constraints.insets = new Insets(5, 5, 5, 5);

        constraints.gridx = 0;
        constraints.gridy = 0;
        add(textField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;

        constraints.insets = new Insets(0, 5, 5, 5);
        add(promptLabel, constraints);

        cardButtonPanel.add(submitButton, "submitButton");
        cardButtonPanel.add(spinner, "spinner");

        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.insets = new Insets(5, 5, 50, 5);
        add(cardButtonPanel, constraints);

        cardKataPanel.add(emptyKataPanel, "empty");
        cardKataPanel.add(invalidKataLabel, "invalid");
        cardKataPanel.add(filledKataPanel, "kata");

        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.insets = new Insets(5, 5, 5, 5);
        add(cardKataPanel, constraints);
    }

    public void startSpinner() {
        cardButtonLayout.show(cardButtonPanel, "spinner");
        revalidate();
        repaint();
    }

    public void stopSpinner() {
        cardButtonLayout.show(cardButtonPanel, "submitButton");
        revalidate();
        repaint();
    }
}
