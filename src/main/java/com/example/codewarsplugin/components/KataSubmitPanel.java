package com.example.codewarsplugin.components;

import com.example.codewarsplugin.models.kata.KataDirectory;
import com.example.codewarsplugin.models.kata.KataInput;
import com.example.codewarsplugin.models.kata.KataRecord;
import com.example.codewarsplugin.models.kata.SubmitResponse;
import com.example.codewarsplugin.services.katas.KataSubmitService;
import com.example.codewarsplugin.services.katas.KataSubmitServiceClient;
import com.example.codewarsplugin.state.Store;

import javax.swing.*;
import java.awt.*;
import java.net.http.HttpResponse;

public class KataSubmitPanel extends JPanel implements KataSubmitServiceClient {


    private Store store;
    private KataDirectory directory;
    private JButton attemptButton = new JButton("Attempt");
    private JButton testButton = new JButton("Test");
    private JButton commitButton = new JButton("Commit");
    private JLabel infoLabel;
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
        setLayout(new GridBagLayout());
        addElementsToPanel();
        addSelectorListeners();
    }

    private void addSelectorListeners() {
        attemptButton.addActionListener((e) -> {
            this.submitService.attempt();
        });

        testButton.addActionListener((e) -> {
            this.submitService.test();
        });
    }

    private void addElementsToPanel() {
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

        add(testButton, constraints);


        constraints.gridx = 0;
        constraints.gridy = 2;

        add(attemptButton, constraints);


        commitButton.setEnabled(false);
        constraints.gridx = 0;
        constraints.gridy = 3;
        add(commitButton, constraints);
    }


    @Override
    public void notifyRunFailed(Exception e) {
        System.out.println("code attempt failed with exception: " + e.getMessage() + "\n");
        e.printStackTrace();
    }

    @Override
    public void notifyAttemptSuccess(SubmitResponse submitResponse) {
        System.out.println("attempt success: " + submitResponse.toString());
    }

    @Override
    public void notifyBadStatusCode(HttpResponse<String> response) {
        System.out.println("Run code bad status code: " + response.statusCode());
    }

    @Override
    public void notifyTestSuccess(SubmitResponse submitResponse) {

    }
}
