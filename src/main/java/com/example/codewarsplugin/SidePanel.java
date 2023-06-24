package com.example.codewarsplugin;

import com.example.codewarsplugin.state.ApplicationState;
import com.example.codewarsplugin.state.views.View;
import com.example.codewarsplugin.state.views.LoginView;
import javax.swing.*;

//177, 54, 30
public class SidePanel extends JPanel {


    public SidePanel() {
        View loginView = new LoginView(this);
        if (loginView.setup()){
            ApplicationState.setView(loginView);
        };
    }
}
