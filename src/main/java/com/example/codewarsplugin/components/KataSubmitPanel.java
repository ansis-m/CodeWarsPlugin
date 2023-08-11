package com.example.codewarsplugin.components;

import com.example.codewarsplugin.SidePanel;
import com.example.codewarsplugin.models.kata.KataDirectory;
import com.example.codewarsplugin.models.kata.KataInput;
import com.example.codewarsplugin.models.kata.KataRecord;
import com.example.codewarsplugin.models.kata.SubmitResponse;
import com.example.codewarsplugin.services.katas.KataSubmitService;
import com.example.codewarsplugin.services.katas.KataSubmitServiceClient;
import com.example.codewarsplugin.state.Store;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.IconLoader;
import com.intellij.ui.AnimatedIcon;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import java.awt.*;
import java.net.http.HttpResponse;
import java.util.ArrayList;

import static com.example.codewarsplugin.config.StringConstants.MESSAGE_ICON;
import static com.example.codewarsplugin.services.utils.Colors.getColor;

public class KataSubmitPanel extends JPanel implements KataSubmitServiceClient {

    private final JButton attemptButton = new JButton("Attempt");
    private final JLabel attemptSpinner = new JLabel(new AnimatedIcon.Big());
    private final JPanel attemptCardPanel = new JPanel();
    private final CardLayout attemptCardLayout = new CardLayout();
    private final JButton testButton = new JButton("Test");
    private final JLabel testSpinner = new JLabel(new AnimatedIcon.Big());
    private final JPanel testCardPanel = new JPanel();
    private final CardLayout testCardLayout = new CardLayout();
    private final JButton commitButton = new JButton("Submit");
    private final JLabel commitSpinner = new JLabel(new AnimatedIcon.Big());
    private final JPanel commitCardPanel = new JPanel();
    private final CardLayout commitCardLayout = new CardLayout();
    private final ArrayList<JButton> buttonList = new ArrayList<>();
    private boolean attemptSuccessful = false;
    private final KataSubmitService submitService;
    private final KataInput input;
    private final KataRecord record;
    private final Store store;

    public KataSubmitPanel(Store store) {
        super();
        this.store = store;
        KataDirectory directory = store.getDirectory();
        this.submitService = new KataSubmitService(store, directory, this);
        this.input = directory.getInput();
        this.record = directory.getRecord();
        addButtonsToList();
        setLayout(new GridBagLayout());
        addElementsToPanel();
        addButtonListeners();
    }

    private void addButtonListeners() {
        attemptButton.addActionListener((e) -> {
            startSpinner(attemptCardLayout, attemptCardPanel);
            store.getManager().getShouldReloadUrl().set(true);
            ApplicationManager.getApplication().executeOnPooledThread(this.submitService::attempt);
        });

        testButton.addActionListener((e) -> {
            startSpinner(testCardLayout, testCardPanel);
            store.getManager().getShouldReloadUrl().set(true);
            ApplicationManager.getApplication().executeOnPooledThread(this.submitService::test);
        });

        commitButton.addActionListener((e) -> {
            startSpinner(commitCardLayout, commitCardPanel);
            store.getManager().getShouldReloadUrl().set(true);
            ApplicationManager.getApplication().executeOnPooledThread(this.submitService::commit);
        });

    }

    private void addElementsToPanel() {

        setupAttemptCardPanel();
        setupTestCardPanel();
        setupCommitCardPanel();
        
        var constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.insets = JBUI.insetsBottom(10);

        JLabel titleLabel = new JLabel(record.getName());
        titleLabel.setFont(titleLabel.getFont().deriveFont(20f));

        constraints.gridx = 0;
        constraints.gridy = 0;
        add(titleLabel, constraints);

        JLabel infoLabel = new JLabel(record.getRank().getName() + " kata in " + record.getSelectedLanguage() + " " + input.getActiveVersion());
        infoLabel.setFont(titleLabel.getFont().deriveFont(15f));
        infoLabel.setForeground(getColor(record.getRank().getColor()));


        constraints.gridx = 0;
        constraints.gridy = 1;
        add(infoLabel, constraints);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(testCardPanel);
        buttonPanel.add(attemptCardPanel);
        buttonPanel.add(commitCardPanel);

        constraints.gridx = 0;
        constraints.gridy = 2;

        add(buttonPanel, constraints);
    }

    @Override
    public void notifyAttemptRunException(Exception e) {
        ApplicationManager.getApplication().invokeLater(() -> {
            attemptSuccessful = false;
            stopSpinner(attemptCardLayout, attemptCardPanel);
            Messages.showMessageDialog("Ups, attempt failed with exception..." + e.getMessage(), "Attempt Failed", IconLoader.getIcon(MESSAGE_ICON, SidePanel.class));
        });
    }

    @Override
    public void notifyAttemptSuccess(SubmitResponse submitResponse) {
        ApplicationManager.getApplication().invokeLater(() -> {
            stopSpinner(attemptCardLayout, attemptCardPanel);
            if (submitResponse.getExitCode() == 0) {
                commitButton.setEnabled(true);
                attemptSuccessful = true;
                Messages.showMessageDialog("Congrats, you passed all the test cases! \nYou may now commit the solution!", "Attempt Success!", IconLoader.getIcon(MESSAGE_ICON, SidePanel.class));
            } else {
                commitButton.setEnabled(false);
                Messages.showMessageDialog("One or several test cases failed. \nSee the test log in test.md", "Attempt Failed!", IconLoader.getIcon(MESSAGE_ICON, SidePanel.class));
            }
        });
    }

    @Override
    public void notifyBadAttemptStatusCode(HttpResponse<String> response) {
        ApplicationManager.getApplication().invokeLater(() -> {
            attemptSuccessful = false;
            stopSpinner(attemptCardLayout, attemptCardPanel);
            Messages.showMessageDialog("Ups, attempt failed with bad status code: " + response.statusCode() + "\nPerhaps log out, and log in would help?!", "Attempt Failed!", IconLoader.getIcon(MESSAGE_ICON, SidePanel.class));
        });
    }

    @Override
    public void notifyTestSuccess(SubmitResponse submitResponse) {
        ApplicationManager.getApplication().invokeLater(() -> {
            attemptSuccessful = false;
            stopSpinner(testCardLayout, testCardPanel);
            if (submitResponse.getExitCode() == 0) {
                Messages.showMessageDialog("All test cases passed!", "Attempt Success!", IconLoader.getIcon(MESSAGE_ICON, SidePanel.class));
            } else {
                Messages.showMessageDialog("One or several test cases failed. \nSee the test log in test.md", "Attempt Failed!", IconLoader.getIcon(MESSAGE_ICON, SidePanel.class));
            }
        });
    }

    @Override
    public void notifyBadTestStatusCode(HttpResponse<String> response) {
        ApplicationManager.getApplication().invokeLater(() -> {
            stopSpinner(testCardLayout, testCardPanel);
            Messages.showMessageDialog("Ups, attempt failed with bad status code: " + response.statusCode() + "\nPerhaps log out, and log in would help?!", "Attempt Failed!", IconLoader.getIcon(MESSAGE_ICON, SidePanel.class));
        });
    }

    @Override
    public void notifyTestRunException(Exception e) {
        ApplicationManager.getApplication().invokeLater(() -> {
            stopSpinner(testCardLayout, testCardPanel);
            Messages.showMessageDialog("Ups, attempt failed with exception..." + e.getMessage(), "Attempt Failed", IconLoader.getIcon(MESSAGE_ICON, SidePanel.class));
        });
    }

    @Override
    public void notifyCommitSuccess(HttpResponse<String> response) {
        ApplicationManager.getApplication().invokeLater(() -> {
            stopSpinner(commitCardLayout, commitCardPanel);
            Messages.showMessageDialog("Congrats, you commited your solution! \nSelect a new Kata!", "Commit Success!", IconLoader.getIcon(MESSAGE_ICON, SidePanel.class));
        });
    }

    @Override
    public void notifyCommitFailed(HttpResponse<String> response) {
        ApplicationManager.getApplication().invokeLater(() -> {
            stopSpinner(commitCardLayout, commitCardPanel);
            Messages.showMessageDialog("Ups, attempt failed with bad status code: " + response.statusCode() + "\nPerhaps log out, and log in would help?!", "Attempt Failed!", IconLoader.getIcon(MESSAGE_ICON, SidePanel.class));
        });
    }

    @Override
    public void notifyCommitException(Exception e) {
        ApplicationManager.getApplication().invokeLater(() -> {
            stopSpinner(commitCardLayout, commitCardPanel);
            Messages.showMessageDialog("Ups, attempt failed with exception..." + e.getMessage(), "Attempt Failed", IconLoader.getIcon(MESSAGE_ICON, SidePanel.class));
        });
    }

    private void setupCommitCardPanel() {
        commitButton.setEnabled(false);
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
        attemptSuccessful = false;
        buttonList.forEach(button -> button.setEnabled(false));
        layout.show(panel, "spinner");
        revalidate();
        repaint();
    }

    public void stopSpinner(CardLayout layout, JPanel panel) {
        buttonList.forEach(button -> button.setEnabled(true));
        if (!attemptSuccessful) {
            commitButton.setEnabled(false);
        }
        layout.show(panel, "button");
        revalidate();
        repaint();
    }

    private void addButtonsToList() {
        buttonList.add(testButton);
        buttonList.add(attemptButton);
        buttonList.add(commitButton);
    }
}
