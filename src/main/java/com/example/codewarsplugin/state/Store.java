package com.example.codewarsplugin.state;

import com.example.codewarsplugin.SidePanel;
import com.example.codewarsplugin.models.kata.KataDirectory;
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

    public final JBCefClient client = JBCefApp.getInstance().createClient();
    public final JBCefBrowser browser = new JBCefBrowserBuilder().setClient(client).setUrl(SIGN_IN_URL).build();

    public Store(SidePanel sidePanel){
        client.setProperty(JS_QUERY_POOL_SIZE, 10);
        this.sidePanel = sidePanel;
        sidePanel.add(tabbedPane, BorderLayout.CENTER);
        manager = new TabManager(this);
        manager.setupDashboard();

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
    public void initTabManager() {
        manager.setupDashboard();
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
}
