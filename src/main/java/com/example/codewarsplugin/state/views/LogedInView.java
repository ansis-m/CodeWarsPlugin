package com.example.codewarsplugin.state.views;

import com.example.codewarsplugin.SidePanel;
import com.example.codewarsplugin.models.user.User;
import com.example.codewarsplugin.services.UserService;
import com.example.codewarsplugin.state.Panels;

import java.awt.*;

public class LogedInView extends View {


    private static User user;

    public LogedInView(SidePanel sidePanel) {
        super(sidePanel);
    }


    public static boolean setup() {
        sidePanel.add(Panels.getKataPrompt(), BorderLayout.CENTER);
        sidePanel.revalidate();
        sidePanel.repaint();
        return true;
    }


    public static boolean cleanUp() {
        return false;
    }


    public static void init() {
        user = UserService.getUser();
    }
}
