package com.example.codewarsplugin.components;

import com.example.codewarsplugin.models.kata.KataDirectory;
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
    private JButton submitButton = new JButton("Submit");
    private KataSubmitService submitService;

    public KataSubmitPanel(Store store) {
        super();
        this.store = store;
        this.directory = store.getDirectory();
        this.submitService = new KataSubmitService(store, directory, this);
        setLayout(new GridBagLayout());
        addElementsToPanel();
        addSelectorListeners();
    }

    private void addSelectorListeners() {
        submitButton.addActionListener((e) -> {
            this.submitService.run();
        });
    }

    private void addElementsToPanel() {
        var constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.insets = new Insets(0, 0, 5, 0);

        constraints.gridx = 0;
        constraints.gridy = 0;

        add(submitButton, constraints);
    }


    @Override
    public void notifyRunFailed(Exception e) {
        System.out.println("code run failed with exception: " + e.getMessage() + "\n");
        e.printStackTrace();
    }

    @Override
    public void notifyRunSuccess(SubmitResponse submitResponse) {
        System.out.println("run success: " + submitResponse.toString());
    }

    @Override
    public void notifyBadStatusCode(HttpResponse<String> response) {
        System.out.println("Run code bad status code: " + response.statusCode());
    }
}
