package com.example.codewarsplugin.components;

import com.example.codewarsplugin.SidePanel;
import com.example.codewarsplugin.models.kata.KataDirectory;
import com.example.codewarsplugin.models.kata.KataInput;
import com.example.codewarsplugin.models.kata.KataRecord;
import com.example.codewarsplugin.models.kata.SubmitResponse;
import com.example.codewarsplugin.services.files.create.AbstractFileService;
import com.example.codewarsplugin.services.files.create.FileServiceFactory;
import com.example.codewarsplugin.services.katas.KataSubmitService;
import com.example.codewarsplugin.services.katas.KataSubmitServiceClient;
import com.example.codewarsplugin.state.Store;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.IconLoader;
import com.intellij.ui.AnimatedIcon;
import com.intellij.util.ui.JBUI;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

import static com.example.codewarsplugin.config.StringConstants.DASHBOARD_URL;
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
    private KataSubmitService submitService;
    private final JButton aboutButton = new JButton("About");
    private final JButton reloadButton = new JButton("Reload codewars");
    private final Store store;
    private final KataDirectory directory;
    private boolean isWorkspaceEmpty = false;

    public KataSubmitPanel(Store store) {
        super();
        this.directory = store.getDirectory();
        this.store = store;

        if (directory == null) {
            isWorkspaceEmpty = true;
        } else {
            KataDirectory directory = store.getDirectory();
            this.submitService = new KataSubmitService(store, directory, this);
        }


        addButtonsToList();
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
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

        reloadButton.addActionListener((e) -> {
            String url = DASHBOARD_URL;
            var dir = store.getDirectory();
            if (dir != null) {
                url = dir.getRecord().getWorkUrl();
            }
            store.getManager().getBrowser().loadURL(url);
        });

        aboutButton.addActionListener((e) -> {
            try {
                InputStream stream = KataSubmitPanel.class.getResourceAsStream("/about.html");
                File tempFile = File.createTempFile("about", ".html");
                Files.copy(stream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                stream.close();
                store.getManager().getBrowser().loadURL(tempFile.toURI().toURL().toString());
            } catch (Exception ignored) {
                ignored.printStackTrace();
            }

        });


    }

    private void addElementsToPanel() {

        if (!isWorkspaceEmpty){
            setupAttemptCardPanel();
            setupTestCardPanel();
            setupCommitCardPanel();
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
            buttonPanel.add(testCardPanel);
            buttonPanel.add(attemptCardPanel);
            buttonPanel.add(commitCardPanel);
            add(buttonPanel);
        }


        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        aboutButton.setToolTipText("Unsure how to use the plugin? Read the \"About\" in the browser!");
        aboutButton.setIcon(AllIcons.Actions.Help);
        rightPanel.add(aboutButton);

        reloadButton.setToolTipText("Reload the page of the current kata or navigate to the codewars.com if the workspace is empty.");
        reloadButton.setIcon(AllIcons.Actions.Refresh);
        rightPanel.add(reloadButton);

        add(Box.createHorizontalGlue());
        add(rightPanel);
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
            store.getManager().reloadBrowser();
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
            store.getManager().reloadBrowser();
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
        try{
            updateRecordFile();
        } catch (Exception ignored){}
        ApplicationManager.getApplication().invokeLater(() -> {
            store.getManager().reloadWorkspace();
            stopSpinner(commitCardLayout, commitCardPanel);
            Messages.showMessageDialog("Congrats, you commited your solution! \nSelect a new Kata!", "Commit Success!", IconLoader.getIcon(MESSAGE_ICON, SidePanel.class));
        });
    }

    private void updateRecordFile() throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        var dir = store.getDirectory();
        dir.getRecord().setCompleted(true);
        AbstractFileService service = FileServiceFactory.createFileService(dir.getInput(), dir.getRecord(), store.getProject());
        WriteCommandAction.runWriteCommandAction(store.getProject(), () -> {
            try {
                service.updateRecord(dir);
            } catch (Exception e) {
                e.printStackTrace();
            }
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
        commitButton.setToolTipText("\"" + directory.getRecord().getName() + "\" a " + directory.getRecord().getRank().getName() + " kata in " + directory.getRecord().getSelectedLanguage() + " " + directory.getInput().getActiveVersion());
        commitButton.setIcon(AllIcons.Actions.Commit);
        commitCardPanel.add(commitButton, "button");
        commitCardPanel.add(commitSpinner, "spinner");
    }

    private void setupTestCardPanel() {
        testCardPanel.setLayout(testCardLayout);
        testButton.setIcon(AllIcons.RunConfigurations.TestState.Run);
        testButton.setToolTipText("\"" + directory.getRecord().getName() + "\" a " + directory.getRecord().getRank().getName() + " kata in " + directory.getRecord().getSelectedLanguage() + " " + directory.getInput().getActiveVersion());
        testCardPanel.add(testButton, "button");
        testCardPanel.add(testSpinner, "spinner");
    }

    private void setupAttemptCardPanel() {
        attemptCardPanel.setLayout(attemptCardLayout);
        attemptButton.setIcon(AllIcons.RunConfigurations.TestState.Run_run);
        attemptButton.setToolTipText("\"" + directory.getRecord().getName() + "\" a " + directory.getRecord().getRank().getName() + " kata in " + directory.getRecord().getSelectedLanguage() + " " + directory.getInput().getActiveVersion());
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
        buttonList.add(aboutButton);
        buttonList.add(reloadButton);
    }
}
