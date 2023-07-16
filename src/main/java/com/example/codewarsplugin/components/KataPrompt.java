package com.example.codewarsplugin.components;

import com.example.codewarsplugin.models.kata.KataRecord;
import com.example.codewarsplugin.services.katas.KataIdService;
import com.example.codewarsplugin.state.Vars;
import com.intellij.ui.AnimatedIcon;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
    private JLabel spinner;
    private GridBagConstraints constraints;
    private Vars vars;


    public KataPrompt(Vars vars){
        super();
        this.vars = vars;
        cardButtonPanel = new JPanel();
        cardButtonLayout = new CardLayout();
        cardKataPanel = new JPanel();
        cardKataLayout = new CardLayout();
        emptyKataPanel = new JPanel();
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
        addButtonPushedListener();
    }

    private void addButtonPushedListener() {
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new KataIdService().getKataRecord(textField.getText(), vars);
                cardButtonLayout.show(cardButtonPanel, "spinner");
                revalidate();
                repaint();
            }
        });
    }

    private void searchKata(ActionEvent event) {
        startSpinner();

    }

    public void complete(KataRecord record){
        System.out.println("complete: " + this+ record);
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

        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.insets = new Insets(5, 5, 5, 5);
        add(cardKataPanel, constraints);
    }

    public void startSpinner() {

    }

    public void stopSpinner() {
        cardButtonLayout.show(cardButtonPanel, "submitButton");
        revalidate();
        repaint();
    }

}
