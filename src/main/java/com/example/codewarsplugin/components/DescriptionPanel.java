package com.example.codewarsplugin.components;

import com.example.codewarsplugin.models.kata.KataDirectory;
import com.example.codewarsplugin.state.Store;

import javax.swing.*;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

        String rawString = directory.getRecord().getDescription();
        String processedString = processLanguages(rawString, directory.getRecord().getSelectedLanguage().toLowerCase());


        return "<html> " + processedString.replaceAll("\\n+", "<br>") + "</html>";

    }

    private String processLanguages(String rawString, String language) {

        ArrayList<String> tokens = extractTokens(rawString);
        for (String token : tokens) {
            System.out.println("token: " + token);
            if (!token.toLowerCase().contains("```" + language) || token.toLowerCase().contains("```" + language + "script")) {
                rawString = rawString.replace(token, "");
                System.out.println("replace token: " + token);
            }
        }
        return rawString.replace("```" + language, "").replace("```", "");
    }

    private static ArrayList<String> extractTokens(String inputString) {
        ArrayList<String> tokens = new ArrayList<>();
        Pattern pattern = Pattern.compile("```([a-zA-Z]+)\\n(.*?)```", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(inputString);

        while (matcher.find()) {
            String token = matcher.group(0);
            tokens.add(token);
        }

        return tokens;
    }




}
