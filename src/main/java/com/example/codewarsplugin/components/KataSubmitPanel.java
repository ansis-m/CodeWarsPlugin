package com.example.codewarsplugin.components;

import com.example.codewarsplugin.models.kata.KataDirectory;
import com.example.codewarsplugin.models.kata.KataInput;
import com.example.codewarsplugin.models.kata.KataRecord;
import com.example.codewarsplugin.models.kata.SubmitResponse;
import com.example.codewarsplugin.models.user.User;
import com.example.codewarsplugin.services.UserService;
import com.example.codewarsplugin.services.katas.KataSubmitService;
import com.example.codewarsplugin.services.katas.KataSubmitServiceClient;
import com.example.codewarsplugin.state.Store;
import com.example.codewarsplugin.state.SyncService;
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
    private JTextPane textPane = new JTextPane();
    private ArrayList<JButton> buttonList = new ArrayList<>();
    private boolean attemptSuccessful = false;
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
        addButtonListeners();
    }

    private void addButtonListeners() {
        attemptButton.addActionListener((e) -> {
            startSpinner(attemptCardLayout, attemptCardPanel);
            this.submitService.attempt();
        });

        testButton.addActionListener((e) -> {
            startSpinner(testCardLayout, testCardPanel);
            this.submitService.test();
        });

        commitButton.addActionListener((e) -> {
            startSpinner(commitCardLayout, commitCardPanel);
            this.submitService.commit();
        });
    }

    private void addElementsToPanel() {

        setupAttemptCardPanel();
        setupTestCardPanel();
        setupCommitCardPanel();
        setupTextPane();
        
        var constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.insets = new Insets(0, 0, 10, 0);

        infoLabel = new JLabel(record.getName() + " in " +record.getSelectedLanguage() + " " + input.getActiveVersion());
        infoLabel.setFont(infoLabel.getFont().deriveFont(20f));

        constraints.gridx = 0;
        constraints.gridy = 0;
        add(infoLabel, constraints);


        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(testCardPanel);
        buttonPanel.add(attemptCardPanel);
        buttonPanel.add(commitCardPanel);

        constraints.gridx = 0;
        constraints.gridy = 1;

        add(buttonPanel, constraints);

        constraints.gridx = 0;
        constraints.gridy = 3;
        add(textPane, constraints);
    }

    private void setupTextPane() {
        textPane.setContentType("text/html");
        textPane.setEditable(false);
        textPane.setPreferredSize(new Dimension(370, 200));
    }

    @Override
    public void notifyAttemptRunException(Exception e) {
        System.out.println("code attempt failed with exception: " + e.getMessage() + "\n");
        attemptSuccessful = false;
        stopSpinner(attemptCardLayout, attemptCardPanel);
        setTextPaneText("#B1361E", "Ups, attempt failed with exception...", e.getMessage());
        resetExitStatusPanel(4000);
    }

    @Override
    public void notifyAttemptSuccess(SubmitResponse submitResponse) {
        stopSpinner(attemptCardLayout, attemptCardPanel);
        if (submitResponse.getExitCode() == 0){
            attemptSuccessful = true;
            setTextPaneText("green", "Congrats, you passed all the test cases!", "You may now commit the solution!");
        } else {
            setTextPaneText("#B1361E","One or several test cases failed.", "See the test log in test.json");
            resetExitStatusPanel(4000);
        }
    }

    @Override
    public void notifyBadAttemptStatusCode(HttpResponse<String> response) {
        attemptSuccessful = false;
        stopSpinner(attemptCardLayout, attemptCardPanel);
        setTextPaneText("#B1361E", "Ups, attempt failed with bad status code: " + response.statusCode(), "Perhaps log out, and log in would help?!");
        resetExitStatusPanel(4000);
    }

    @Override
    public void notifyTestSuccess(SubmitResponse submitResponse) {
        attemptSuccessful = false;
        stopSpinner(testCardLayout, testCardPanel);
        if (submitResponse.getExitCode() == 0) {
            setTextPaneText("green","All test cases passed!");
        } else {
            setTextPaneText("#B1361E","One or several test cases failed.", "See the test log in test.json");
        }
        resetExitStatusPanel(3600);
    }

    @Override
    public void notifyBadTestStatusCode(HttpResponse<String> response) {
        stopSpinner(testCardLayout, testCardPanel);
        setTextPaneText("#B1361E", "Ups, attempt failed with bad status code " + response.statusCode(), "Perhaps log out, and log in would help?!");
        resetExitStatusPanel(4000);
    }

    @Override
    public void notifyTestRunException(Exception e) {
        stopSpinner(testCardLayout, testCardPanel);
        setTextPaneText("#B1361E","Ups, test run failed with exception: ", e.getMessage());
        resetExitStatusPanel(3000);
    }

    @Override
    public void notifyCommitSuccess(HttpResponse<String> response) {
        stopSpinner(commitCardLayout, commitCardPanel);

        int honor = UserService.getUser().getHonor();
        UserService.clearUser();
        User newUser = UserService.getUser();

        store.getCurrentView().refreshUserPanel();
        setTextPaneText("green", "Commit success!!!", "Your honor increased from " + honor + " to " + newUser.getHonor() + "!");

        resetExitStatusPanel(5000);
    }

    @Override
    public void notifyCommitFailed(HttpResponse<String> response) {
        System.out.println("failed body: " + response.body());
        stopSpinner(commitCardLayout, commitCardPanel);
        setTextPaneText("#B1361E", "Ups, attempt failed with bad status code " + response.statusCode(), "Perhaps log out, and log in would help?!");
        resetExitStatusPanel(4000);
    }

    @Override
    public void notifyCommitException(Exception e) {
        stopSpinner(commitCardLayout, commitCardPanel);
        setTextPaneText("#B1361E", "Ups, commit failed with exception...", e.getMessage());
        resetExitStatusPanel(4000);
        System.out.println("commit exception: ");
        e.printStackTrace();
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
        SwingUtilities.invokeLater(() -> {
            attemptSuccessful = false;
            buttonList.forEach(button -> button.setEnabled(false));
            layout.show(panel, "spinner");
            revalidate();
            repaint();
        });
    }

    public void stopSpinner(CardLayout layout, JPanel panel) {
        SwingUtilities.invokeLater(() -> {
            buttonList.forEach(button -> button.setEnabled(true));
            if (!attemptSuccessful) {
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

    private void resetExitStatusPanel(int time){
        Timer timer = new Timer(time, e -> SwingUtilities.invokeLater(() -> {
            setTextPaneText("white","");
            revalidate();
            repaint();
        }));
        timer.setRepeats(false);
        timer.start();
    }


    private void setTextPaneText(String color, String... args){

        StringBuilder builder = new StringBuilder("<html><body style='font-family: " + UIManager.getFont("Label.font").getFamily()+ "; font-size: 13px; color: " + color + ";'><div style='text-align: center;'>");
        for(var arg : args) {
            builder.append(arg);
            builder.append("<br>");
        }
        builder.append("</div></body></html>");
        textPane.setText(builder.toString());
        revalidate();
        repaint();
    }
}
