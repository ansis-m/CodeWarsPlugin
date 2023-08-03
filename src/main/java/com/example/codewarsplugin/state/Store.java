package com.example.codewarsplugin.state;

import com.example.codewarsplugin.SidePanel;
import com.example.codewarsplugin.models.kata.KataDirectory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.components.JBTabbedPane;
import com.intellij.ui.jcef.JBCefApp;
import com.intellij.ui.jcef.JBCefBrowser;
import com.intellij.ui.jcef.JBCefBrowserBuilder;
import com.intellij.ui.jcef.JBCefClient;

import java.awt.*;


import static com.example.codewarsplugin.config.StringConstants.SIGN_IN_URL;
import static com.intellij.ui.jcef.JBCefClient.Properties.JS_QUERY_POOL_SIZE;

public class Store {

    private SidePanel sidePanel;
    private KataDirectory directory;
    private final JBTabbedPane tabbedPane = new JBTabbedPane();
    private final TabManager manager;
    private final JBCefClient client = JBCefApp.getInstance().createClient();
    private final JBCefBrowser browser = new JBCefBrowserBuilder().setClient(client).setUrl(SIGN_IN_URL).build();
    private final Project project;


    public Store(SidePanel sidePanel, Project project, ToolWindow toolWindow) {
        client.setProperty(JS_QUERY_POOL_SIZE, 10);
        this.sidePanel = sidePanel;
        sidePanel.add(tabbedPane, BorderLayout.CENTER);
        manager = new TabManager(this, project, toolWindow);
        manager.setupDashboard();
        this.project = project;
    }

    public SidePanel getSidePanel() {
        return sidePanel;
    }
    public void setCurrentKataDirectory(KataDirectory directory) {
        this.directory = directory;
    }
    public KataDirectory getDirectory() {
        return directory;
    }

    public JBTabbedPane getTabbedPane() {
        return tabbedPane;
    }
    public TabManager getManager() {
        return manager;
    }

    public JBCefClient getClient() {
        return client;
    }

    public JBCefBrowser getBrowser() {
        return browser;
    }

    public Project getProject() {
        return project;
    }
}
