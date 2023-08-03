package com.example.codewarsplugin;

import com.example.codewarsplugin.state.Store;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import org.cef.network.CefCookieManager;

import javax.swing.*;
import java.awt.*;

public class SidePanel extends JPanel {
    private Store store;

    public SidePanel(Project project, ToolWindow toolWindow) {
        setLayout(new BorderLayout());
        initPlugin(project, toolWindow);

    }

    private void initPlugin(Project project, ToolWindow toolWindow) {
        store = new Store(this, project, toolWindow);
        Runtime.getRuntime().addShutdownHook(new Thread(this::cleanup));
    }

    private void cleanup() {
        CefCookieManager.getGlobalManager().deleteCookies(null, null);
    }

}
