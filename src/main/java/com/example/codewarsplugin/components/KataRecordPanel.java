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

public class KataRecordPanel extends JPanel implements KataRecordServiceClient {

    private JTextField textField = new JTextField(30);
    private JButton submitButton = new JButton("Get Kata");
    private JLabel promptLabel = new JLabel("Paste kata title!");
    private JLabel kataNotFoundLabel = new JLabel("Kata not found!");
    private JPanel cardButtonPanel = new JPanel();
    private CardLayout cardButtonLayout = new CardLayout();
    private JPanel cardKataPanel = new JPanel();
    private CardLayout cardKataLayout = new CardLayout();
    private JPanel emptyKataInputPanel = new JPanel();
    private JPanel dummyKataInputPanel;
    private JPanel filledKataInputPanel;
    private JLabel spinner = new JLabel(new AnimatedIcon.Big());
    private GridBagConstraints constraints = new GridBagConstraints();
    private Vars vars;

    public KataRecordPanel(Vars vars){
        super();
        this.vars = vars;
        filledKataInputPanel = new KataInputPanel(null, vars);
        dummyKataInputPanel = new KataInputPanel(null, vars);
        setLayout(new GridBagLayout());
        addElementsToPanel();
        addKataRecordSearchListeners(this);
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

        kataNotFoundLabel.setForeground(Color.red);
        kataNotFoundLabel.setFont(promptLabel.getFont().deriveFont(15f));
        cardKataPanel.setLayout(cardKataLayout);

        cardKataPanel.add(emptyKataInputPanel, "empty");
        cardKataPanel.add(kataNotFoundLabel, "invalid");
        cardKataPanel.add(filledKataInputPanel, "kata");
        cardKataPanel.add(dummyKataInputPanel, "dummy");

        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.insets = new Insets(5, 5, 5, 5);
        add(cardKataPanel, constraints);
    }

    private void addKataRecordSearchListeners(KataRecordPanel kataRecordPanel) {
        submitButton.addActionListener(e -> {
            startSpinner();
            KataRecordService.getKataRecord(textField.getText(), kataRecordPanel);
        });
        textField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    startSpinner();
                    KataRecordService.getKataRecord(textField.getText(), kataRecordPanel);
                }
            }
            @Override
            public void keyReleased(KeyEvent e) {}
        });
    }

    @Override
    public void processKataRecord(KataRecord record){
        System.out.println("processKataRecord: " + record);
        stopSpinner();
        cardKataPanel.remove(filledKataInputPanel);
        filledKataInputPanel = new KataInputPanel(record, vars);
        cardKataPanel.add(filledKataInputPanel, "kata");
        cardKataLayout.show(cardKataPanel, "kata");
        cardKataPanel.revalidate();
        cardKataPanel.repaint();
    }

    @Override
    public void processKataRecordNotFound(Exception exception) {
        stopSpinner();
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
