package com.example.codewarsplugin.components;

import com.example.codewarsplugin.services.LoginService;
import groovy.transform.EqualsAndHashCode;


import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Arrays;

@EqualsAndHashCode(callSuper = true)
public class LoginManager extends JPanel{

    public static JPasswordField passwordField;
    public static JTextField textField;
    public static JButton submitButton;
    public static JLabel promptLabel;


    public LoginManager(){
        super();
        setLayout(new GridBagLayout());
        textField = new JTextField(10);
        passwordField = new JPasswordField(10);
        submitButton = new JButton("SIGN IN");
        promptLabel = new JLabel("sign in with your Codewars credentials");
        textField.setText("email");
        passwordField.setText("password");
        passwordField.setEchoChar((char) 0);
        passwordField.setFont(textField.getFont());
        addListener(textField, "email");
        addListener(passwordField, "password");
        addEnterKeyListener();
        addElementsToPanel();
    }


    private void addElementsToPanel() {

        promptLabel.setFont(promptLabel.getFont().deriveFont(10f));

        GridBagConstraints constraints = new GridBagConstraints();
        constraints.anchor = GridBagConstraints.CENTER;
        constraints.insets = new Insets(5, 5, 5, 5);

        constraints.gridx = 0;
        constraints.gridy = 0;
        add(textField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 1;
        add(passwordField, constraints);

        constraints.gridx = 0;
        constraints.gridy = 2;

        constraints.insets = new Insets(10, 5, 5, 5);
        add(submitButton, constraints);


        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.insets = new Insets(5, 5, 50, 5);
        add(promptLabel, constraints);
    }


    private static void addListener(JTextComponent textField, String input) {
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

    private static void addEnterKeyListener(){
        passwordField.addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    System.out.println("Enter key pressed. Input:");
                    if (LoginService.login(textField.getText(), Arrays.toString(passwordField.getPassword()))){
                        System.out.println("Login success!");
                    }
                }
            }
            @Override
            public void keyReleased(KeyEvent e) {
            }
        });
    }
}
