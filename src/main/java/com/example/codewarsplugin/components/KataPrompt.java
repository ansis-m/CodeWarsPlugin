package com.example.codewarsplugin.components;

import com.example.codewarsplugin.models.kata.KataRecord;
import com.example.codewarsplugin.services.katas.KataRecordService;
import com.example.codewarsplugin.services.katas.KataRecordServiceClient;
import com.example.codewarsplugin.state.Vars;
import com.intellij.ui.AnimatedIcon;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KataPrompt extends JPanel implements KataRecordServiceClient {

    private JTextField textField = new JTextField(30);
    private JButton submitButton = new JButton("Get Kata");
    private JLabel promptLabel = new JLabel("Paste kata title!");
    private JLabel invalidKataLabel = new JLabel("Kata not found!");
    private JPanel cardButtonPanel = new JPanel();
    private CardLayout cardButtonLayout = new CardLayout();
    private JPanel cardKataPanel = new JPanel();
    private CardLayout cardKataLayout = new CardLayout();
    private JPanel emptyKataPanel = new JPanel();
    private JPanel filledKataPanel;
    private JLabel spinner = new JLabel(new AnimatedIcon.Big());
    private GridBagConstraints constraints = new GridBagConstraints();
    private KataRecord record;
    private Vars vars;

    public KataPrompt(Vars vars){
        super();
        this.vars = vars;
        filledKataPanel = new KataRecordPanel(null, vars);
        setLayout(new GridBagLayout());
        addElementsToPanel();
        addKataRecordSearchListeners(this);
    }

    private void addKataRecordSearchListeners(KataPrompt kataPrompt) {
        submitButton.addActionListener(e -> {
            startSpinner();
            KataRecordService.getKataRecord(textField.getText(), kataPrompt);
        });
        textField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    startSpinner();
                    KataRecordService.getKataRecord(textField.getText(), kataPrompt);
                }
            }
            @Override
            public void keyReleased(KeyEvent e) {}
        });
    }

    @Override
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
            filledKataPanel = new KataRecordPanel(record, vars);
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

        cardButtonPanel.setLayout(cardButtonLayout);
        cardButtonPanel.add(submitButton, "submitButton");
        cardButtonPanel.add(spinner, "spinner");

        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.insets = new Insets(5, 5, 50, 5);
        add(cardButtonPanel, constraints);

        invalidKataLabel.setForeground(Color.red);
        invalidKataLabel.setFont(promptLabel.getFont().deriveFont(15f));
        cardKataPanel.setLayout(cardKataLayout);

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
