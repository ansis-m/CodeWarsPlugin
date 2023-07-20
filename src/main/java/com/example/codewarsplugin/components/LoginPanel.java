package com.example.codewarsplugin.components;

import com.example.codewarsplugin.services.login.LoginService;
import com.example.codewarsplugin.services.login.LoginServiceClient;
import com.example.codewarsplugin.state.Store;
import com.example.codewarsplugin.state.SyncService;
import com.intellij.ui.AnimatedIcon;
import groovy.transform.EqualsAndHashCode;


import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.*;

@EqualsAndHashCode(callSuper = true)
public class LoginPanel extends JPanel implements LoginServiceClient {

    public JPasswordField passwordField = new JPasswordField(20);
    public JTextField textField = new JTextField(20);
    public JButton submitButton = new JButton("SIGN IN");
    public JLabel promptLabel = new JLabel("Sign in with your Codewars credentials!");
    private GridBagConstraints constraints = new GridBagConstraints();
    private JLabel spinner = new JLabel(new AnimatedIcon.Big());
    private JPanel cardPanel = new JPanel();
    private CardLayout cardLayout = new CardLayout();
    private Store store;


    public LoginPanel(Store store){
        super();
        this.store = store;
        setLayout(new GridBagLayout());
        addListener(textField, "email");
        addListener(passwordField, "password");
        addEnterKeyListener(this);
        addButtonPushedListener();
        addElementsToPanel();
    }

    private void addElementsToPanel() {


        setupTextPasswordPrompt();

        constraints.anchor = GridBagConstraints.CENTER;
        constraints.insets = new Insets(5, 5, 5, 5);

        constraints.gridx = 0;
        constraints.gridy = 0;
        add(textField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        add(passwordField, constraints);

        setupCardPanel();

        constraints.gridx = 0;
        constraints.gridy = 2;
        constraints.weighty = 1.0;
        constraints.insets = new Insets(10, 5, 5, 5);
        add(cardPanel, constraints);

        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.weighty = 0.0;
        constraints.insets = new Insets(10, 5, 50, 5);
        constraints.ipady = 0;
        add(promptLabel, constraints);
    }

    private void setupTextPasswordPrompt() {
        textField.setText("email");
        passwordField.setText("password");
        passwordField.setEchoChar((char) 0);
        passwordField.setFont(textField.getFont());
        promptLabel.setFont(promptLabel.getFont().deriveFont(10f));
    }

    private void setupCardPanel() {
        cardPanel.setLayout(cardLayout);
        cardPanel.add(submitButton, "submitButton");
        cardPanel.add(spinner, "spinner");
    }

    public void showLoginFailLabel(String message){

        var defaultColor = promptLabel.getForeground();
        var defaultText = promptLabel.getText();
        promptLabel.setText(message);
        promptLabel.setForeground(Color.red);
        revalidate();
        repaint();
        Timer timer = new Timer(2500, e -> SwingUtilities.invokeLater(() -> {
            promptLabel.setText(defaultText);
            promptLabel.setForeground(defaultColor);
            revalidate();
            repaint();
        }));
        timer.setRepeats(false);
        timer.start();
    }


    private void addListener(JTextComponent textField, String input) {
        textField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textField.getText().equals(input)) {
                    textField.setText("");
                    if (textField instanceof JPasswordField) {
                        ((JPasswordField) textField).setEchoChar('*');
                    }
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    textField.setText(input);
                    if (textField instanceof JPasswordField) {
                        ((JPasswordField) textField).setEchoChar((char) 0);
                    }
                } else if (textField instanceof JPasswordField) {
                    ((JPasswordField) textField).setEchoChar('*');
                }
            }
        });
    }

    private void addEnterKeyListener(LoginServiceClient client){
        passwordField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    SyncService.startLoginSpinner();
                    LoginService.login(textField.getText(), new String(passwordField.getPassword()), client);
                }
            }
            @Override
            public void keyReleased(KeyEvent e) {
            }
        });
    }

    private void addButtonPushedListener() {
        submitButton.addActionListener(e -> {
            SyncService.startLoginSpinner();
            LoginService.login(textField.getText(), new String(passwordField.getPassword()), this);
        });
    }

    public void startSpinner() {
        cardLayout.show(cardPanel, "spinner");
        revalidate();
        repaint();
    }

    public void stopSpinner() {
        cardLayout.show(cardPanel, "submitButton");
        revalidate();
        repaint();
    }
}
