package com.example.codewarsplugin.components;

import com.example.codewarsplugin.state.Vars;

import javax.swing.*;
import java.awt.*;

public class NavigationBar extends JPanel {


    private Vars vars;
    private JButton backButton = new JButton("BACK");

    public NavigationBar(Vars vars) {
        super();
        this.vars = vars;
        setLayout(new FlowLayout(FlowLayout.RIGHT, 20, 20));
        addListener();
        add(backButton);
    }

    private void addListener() {
        backButton.addActionListener((e) -> {
            var previousView = vars.getPreviousView();


            vars.getCurrentView().cleanup();
            vars.getPreviousViews().add(vars.getCurrentView());

            previousView.setup();
            vars.setCurrentView(previousView);

            vars.getSidePanel().revalidate();
            vars.getSidePanel().repaint();
        });


    }
}
