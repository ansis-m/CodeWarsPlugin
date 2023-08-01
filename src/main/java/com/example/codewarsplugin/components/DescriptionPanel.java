package com.example.codewarsplugin.components;

import com.example.codewarsplugin.models.kata.KataDirectory;
import com.example.codewarsplugin.state.Store;

import javax.swing.*;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;

public class DescriptionPanel extends JPanel {

    private final Store store;
    private final KataDirectory directory;

    public DescriptionPanel(Store store) {
        this.store = store;
        directory = store.getDirectory();
        setLayout(new GridBagLayout());
        addComponents();
    }

    private void addComponents() {
        var constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.insets = new Insets(0, 10, 0, 10);

        constraints.gridx = 0;
        constraints.gridy = 0;


        JEditorPane editorPane = new JEditorPane();
        editorPane.setEditable(false);
        editorPane.setContentType("text/html");

        HTMLEditorKit editorKit = new HTMLEditorKit();
        editorKit.getStyleSheet().addRule("pre { white-space: pre-wrap; }");
        editorPane.setEditorKit(editorKit);

        editorPane.setText(getHtmlContent());
        add(editorPane, constraints);
    }

    private String getHtmlContent() {
        return "<html> " + directory.getRecord().getDescription().replaceAll("\n", "<br>") + "</html>";

    }


}
