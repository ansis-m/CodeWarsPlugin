package com.example.codewarsplugin.components;

import com.example.codewarsplugin.models.kata.KataDirectory;
import com.example.codewarsplugin.models.kata.KataInput;
import com.example.codewarsplugin.models.kata.KataRecord;
import com.example.codewarsplugin.services.files.create.FileManager;
import com.example.codewarsplugin.services.files.create.FileServiceClient;
import com.example.codewarsplugin.services.katas.KataInputService;
import com.example.codewarsplugin.services.katas.KataInputServiceClient;
import com.example.codewarsplugin.state.Store;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.AnimatedIcon;
import com.intellij.ui.JBColor;

import javax.swing.*;
import java.awt.*;

import static com.example.codewarsplugin.services.utils.Colors.getColor;

public class KataInputPanel extends JPanel implements KataInputServiceClient, FileServiceClient {

    private KataRecord record;
    private Store store;
    private JButton button = new JButton("Setup Kata");
    private ComboBox<String> languageBox;
    private JPanel cardButtonPanel = new JPanel();
    private CardLayout cardButtonLayout = new CardLayout();
    private JLabel spinner = new JLabel(new AnimatedIcon.Big());
    private JLabel failMessage = new JLabel("\u00A0");

    private JPanel setupPanel = new JPanel();

    public KataInputPanel(KataRecord record, Store store){
        super();
        this.record = record;
        this.store = store;
        setLayout(new GridBagLayout());
        setupButtonListeners();
        setupCardPanel();
        addComponents();
    }

    private void setupCardPanel() {
        cardButtonPanel.setLayout(cardButtonLayout);
        cardButtonPanel.add(button, "button");
        cardButtonPanel.add(spinner, "spinner");
    }

    private void addComponents() {
        var constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.insets = new Insets(0, 0, 5, 0);

        constraints.gridx = 0;
        constraints.gridy = 0;


        setupPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));

        var label = new JLabel(record == null ? "" : record.getName());
        label.setFont(label.getFont().deriveFont(14f));
        setupPanel.add(label, constraints);

        languageBox = new ComboBox<>(record == null ? new String[]{} : record.getLanguages());
        setupPanel.add(languageBox, constraints);

        setupPanel.add(cardButtonPanel, constraints);


        constraints.gridx = 0;
        constraints.gridy = 1;
        add(setupPanel, constraints);

        constraints.gridx = 0;
        constraints.gridy = 2;
        add(failMessage, constraints);
    }

    private void setupButtonListeners() {

        button.addActionListener((e) -> {
            String language = (String) languageBox.getSelectedItem();
            record.setSelectedLanguage(language);
            KataInputService.getKata(record, this);
        });

    }

    @Override
    public void processKataInput(KataInput kataInput) {
        ApplicationManager.getApplication().invokeLater(() -> new FileManager().createFile(kataInput, record, store, this));
    }

    @Override
    public void processInputNotFound(Exception exception) {

        failMessage.setText("Setup failed: " + exception.getMessage());
        failMessage.setForeground(getColor("red"));
        revalidate();
        repaint();
        Timer timer = new Timer(2500, e -> SwingUtilities.invokeLater(() -> {
            failMessage.setText("\u00A0");
            revalidate();
            repaint();
        }));
        timer.setRepeats(false);
        timer.start();
    }

    @Override
    public void startSpinner() {
        cardButtonLayout.show(cardButtonPanel, "spinner");
        revalidate();
        repaint();

    }

    @Override
    public void stopSpinner() {
        cardButtonLayout.show(cardButtonPanel, "button");
        revalidate();
        repaint();
    }

    @Override
    public void notifyFileExists() {

        failMessage.setText("This kata has been configured before in this project.");
        failMessage.setForeground(getColor("red"));
        revalidate();
        repaint();
        Timer timer = new Timer(2500, e -> SwingUtilities.invokeLater(() -> {
            failMessage.setText("\u00A0");
            revalidate();
            repaint();
        }));
        timer.setRepeats(false);
        timer.start();
    }

    @Override
    public void transitionToWorkView(KataDirectory directory) {
        store.setCurrentKataDirectory(directory);
        store.getCurrentView().cleanup();
        store.getPreviousViews().add(store.getCurrentView());
        store.getWorkView().setup();
        store.setCurrentView(store.getWorkView());
    }

    @Override
    public void notifyKataDirectoryCreationFailed() {

    }
}
