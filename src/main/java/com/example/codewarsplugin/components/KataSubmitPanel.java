package com.example.codewarsplugin.components;

import com.example.codewarsplugin.models.kata.KataDirectory;
import com.example.codewarsplugin.models.kata.KataInput;
import com.example.codewarsplugin.models.kata.KataRecord;
import com.example.codewarsplugin.models.kata.SubmitResponse;
import com.example.codewarsplugin.services.katas.KataSubmitService;
import com.example.codewarsplugin.services.katas.KataSubmitServiceClient;
import com.example.codewarsplugin.state.Store;
import com.intellij.ui.AnimatedIcon;

import javax.swing.*;
import java.awt.*;
import java.net.http.HttpResponse;
import java.util.ArrayList;

public class KataSubmitPanel extends JPanel implements KataSubmitServiceClient {


    private Store store;
    private KataDirectory directory;
    private JButton attemptButton = new JButton("Attempt");
    private JLabel attemptSpinner = new JLabel(new AnimatedIcon.Big());
    private JPanel attemptCardPanel = new JPanel();
    private CardLayout attemptCardLayout = new CardLayout();
    private JButton testButton = new JButton("Test");
    private JLabel testSpinner = new JLabel(new AnimatedIcon.Big());
    private JPanel testCardPanel = new JPanel();
    private CardLayout testCardLayout = new CardLayout();
    private JButton commitButton = new JButton("Commit");
    private JLabel commitSpinner = new JLabel(new AnimatedIcon.Big());
    private JPanel commitCardPanel = new JPanel();
    private CardLayout commitCardLayout = new CardLayout();
    private JLabel infoLabel;
    private ArrayList<JButton> buttonList = new ArrayList<>();

    private boolean attempSuccessful = false;
    private KataSubmitService submitService;
    private KataInput input;
    private KataRecord record;

    public KataSubmitPanel(Store store) {
        super();
        this.store = store;
        this.directory = store.getDirectory();
        this.submitService = new KataSubmitService(store, directory, this);
        this.input = directory.getInput();
        this.record = directory.getRecord();
        addButtonsToList();
        setLayout(new GridBagLayout());
        addElementsToPanel();
        addSelectorListeners();
    }

    private void addSelectorListeners() {
        attemptButton.addActionListener((e) -> {
            startSpinner(attemptCardLayout, attemptCardPanel);
            this.submitService.attempt();
        });

        testButton.addActionListener((e) -> {
            startSpinner(testCardLayout, testCardPanel);
            this.submitService.test();
        });
    }

    private void addElementsToPanel() {

        setupAttemptCardPanel();
        setupTestCardPanel();
        setupCommitCardPanel();



        var constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.insets = new Insets(0, 0, 10, 0);

        infoLabel = new JLabel(record.getName() + " in " +record.getSelectedLanguage() + " " + input.getActiveVersion());
        infoLabel.setFont(infoLabel.getFont().deriveFont(20f));

        constraints.gridx = 0;
        constraints.gridy = 0;
        add(infoLabel, constraints);


        constraints.gridx = 0;
        constraints.gridy = 1;

        add(testCardPanel, constraints);


        constraints.gridx = 0;
        constraints.gridy = 2;

        add(attemptCardPanel, constraints);


        commitButton.setEnabled(false);
        constraints.gridx = 0;
        constraints.gridy = 3;
        add(commitCardPanel, constraints);
    }

    @Override
    public void notifyAttemptRunFailed(Exception e) {
        System.out.println("code attempt failed with exception: " + e.getMessage() + "\n");
        e.printStackTrace();
        attempSuccessful = false;
        stopSpinner(attemptCardLayout, attemptCardPanel);
    }

    @Override
    public void notifyAttemptSuccess(SubmitResponse submitResponse) {
        System.out.println("attempt success: " + submitResponse.toString());
        attempSuccessful = true;
        stopSpinner(attemptCardLayout, attemptCardPanel);
    }

    @Override
    public void notifyBadAttemptStatusCode(HttpResponse<String> response) {
        System.out.println("Run code bad status code: " + response.statusCode());
        attempSuccessful = false;
        stopSpinner(attemptCardLayout, attemptCardPanel);
    }

    @Override
    public void notifyTestSuccess(SubmitResponse submitResponse) {
        attempSuccessful = false;
        stopSpinner(testCardLayout, testCardPanel);
    }

    @Override
    public void notifyBadTestStatusCode(HttpResponse<String> response) {
        attempSuccessful = false;
        stopSpinner(testCardLayout, testCardPanel);
    }

    @Override
    public void notifyTestRunFailed(Exception e) {
        attempSuccessful = false;
        stopSpinner(testCardLayout, testCardPanel);
    }

    private void setupCommitCardPanel() {
        commitCardPanel.setLayout(commitCardLayout);
        commitCardPanel.add(commitButton, "button");
        commitCardPanel.add(commitSpinner, "spinner");
    }

    private void setupTestCardPanel() {
        testCardPanel.setLayout(testCardLayout);
        testCardPanel.add(testButton, "button");
        testCardPanel.add(testSpinner, "spinner");
    }

    private void setupAttemptCardPanel() {
        attemptCardPanel.setLayout(attemptCardLayout);
        attemptCardPanel.add(attemptButton, "button");
        attemptCardPanel.add(attemptSpinner, "spinner");
    }

    public void startSpinner(CardLayout layout, JPanel panel) {
        SwingUtilities.invokeLater(() -> {
            attempSuccessful = false;
            buttonList.forEach(button -> button.setEnabled(false));
            layout.show(panel, "spinner");
            revalidate();
            repaint();
        });
    }

    public void stopSpinner(CardLayout layout, JPanel panel) {
        SwingUtilities.invokeLater(() -> {
            buttonList.forEach(button -> button.setEnabled(true));
            if (!attempSuccessful) {
                commitButton.setEnabled(false);
            }
            layout.show(panel, "button");
            revalidate();
            repaint();
        });
    }

    private void addButtonsToList() {
        buttonList.add(testButton);
        buttonList.add(attemptButton);
        buttonList.add(commitButton);
    }
}
