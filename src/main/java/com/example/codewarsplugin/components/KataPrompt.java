package com.example.codewarsplugin.components;

import com.example.codewarsplugin.models.kata.KataRecord;
import com.example.codewarsplugin.services.katas.KataIdService;
import com.example.codewarsplugin.state.Panels;
import com.intellij.ui.AnimatedIcon;

import javax.swing.*;
import java.awt.*;

public class KataPrompt extends JPanel {

    public static JTextField textField;
    public static JButton submitButton;
    public static JLabel promptLabel;
    public static JLabel invalidKataLabel;
    private static JLabel waitLabel = new JLabel(new AnimatedIcon.Big());
    private static final GridBagConstraints constraints = new GridBagConstraints();


    public KataPrompt(){
        super();
        setLayout(new GridBagLayout());
        textField = new JTextField(30);
        submitButton = new JButton("Get Kata");
        promptLabel = new JLabel("Paste kata title!");
        invalidKataLabel = new JLabel("Kata not found!");
        invalidKataLabel.setForeground(Color.red);
        invalidKataLabel.setFont(promptLabel.getFont().deriveFont(15f));
        invalidKataLabel.setVisible(false);
        waitLabel.setVisible(false);
        //addEnterKeyListener();
        addElementsToPanel();
        addButtonPushedListener();
    }

    private void addButtonPushedListener() {
        submitButton.addActionListener((event) -> {
            startSpinner();
            KataRecord record = KataIdService.getKataRecord(textField.getText());
            System.out.println(record);
        });
    }

    public static void complete(boolean success){
        System.out.println("complete: " + KataIdService.record);
        Panels.getKataPrompt().stopSpinner();
        if (!success) {
            invalidKataLabel.setVisible(true);
            Panels.getSidePanel().revalidate();
            Panels.getSidePanel().repaint();
            Timer timer = new Timer(2000, e -> SwingUtilities.invokeLater(() -> {
                invalidKataLabel.setVisible(false);
                Panels.getSidePanel().revalidate();
                Panels.getSidePanel().repaint();
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

        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.insets = new Insets(5, 5, 50, 5);
        add(submitButton, constraints);
        add(waitLabel, constraints);

        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.insets = new Insets(5, 5, 5, 5);
        add(invalidKataLabel, constraints);

    }

    public void startSpinner() {
        submitButton.setVisible(false);
        waitLabel.setVisible(true);
        revalidate();
        repaint();
    }

    public void stopSpinner() {
        submitButton.setVisible(true);
        waitLabel.setVisible(false);
        revalidate();
        repaint();
    }

}
