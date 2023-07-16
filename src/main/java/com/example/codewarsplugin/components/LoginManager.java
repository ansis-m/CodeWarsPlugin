package com.example.codewarsplugin.components;

import com.example.codewarsplugin.services.LoginService;
import com.example.codewarsplugin.state.SyncService;
import com.example.codewarsplugin.state.Vars;
import com.intellij.ui.AnimatedIcon;
import groovy.transform.EqualsAndHashCode;


import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.*;

@EqualsAndHashCode(callSuper = true)
public class LoginManager extends JPanel{

    public JPasswordField passwordField;
    public JTextField textField;
    public JButton submitButton;
    public JLabel promptLabel;
    public JLabel invalidPasswordLabel;
    private GridBagConstraints constraints;
    private JLabel spinner;
    private JPanel cardPanel;
    private CardLayout cardLayout;
    private Vars vars;


    public LoginManager(Vars vars){
        super();
        this.vars = vars;
        constraints = new GridBagConstraints();
        spinner = new JLabel(new AnimatedIcon.Big());
        setLayout(new GridBagLayout());
        textField = new JTextField(20);
        passwordField = new JPasswordField(20);
        submitButton = new JButton("SIGN IN");
        promptLabel = new JLabel("Sign in with your Codewars credentials!");
        textField.setText("email");
        passwordField.setText("password");
        passwordField.setEchoChar((char) 0);
        passwordField.setFont(textField.getFont());
        cardPanel = new JPanel();
        cardLayout = new CardLayout();
        cardPanel.setLayout(cardLayout);
        addListener(textField, "email");
        addListener(passwordField, "password");
        addEnterKeyListener();
        addButtonPushedListener();
        addElementsToPanel();
    }

    private void addElementsToPanel() {

        promptLabel.setFont(promptLabel.getFont().deriveFont(10f));

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

    private void setupCardPanel() {
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
        Timer timer = new Timer(1500, e -> SwingUtilities.invokeLater(() -> {
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

    private void addEnterKeyListener(){
        passwordField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    SyncService.startLoginSpinner();
                    LoginService.login(textField.getText(), new String(passwordField.getPassword()), vars);
                }
            }
            @Override
            public void keyReleased(KeyEvent e) {
            }
        });
    }

    private void addButtonPushedListener() {
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SyncService.startLoginSpinner();
                LoginService.login(textField.getText(), new String(passwordField.getPassword()), vars);
            }
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
