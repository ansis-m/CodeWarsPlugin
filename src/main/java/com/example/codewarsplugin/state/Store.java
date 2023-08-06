package com.example.codewarsplugin.state;

import com.example.codewarsplugin.SidePanel;
import com.example.codewarsplugin.components.OverlaySpinner;
import com.example.codewarsplugin.models.kata.KataDirectory;
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBTabbedPane;
import com.intellij.ui.jcef.JBCefApp;
import com.intellij.ui.jcef.JBCefBrowser;
import com.intellij.ui.jcef.JBCefBrowserBuilder;
import com.intellij.ui.jcef.JBCefClient;
import org.cef.network.CefCookieManager;

import java.awt.*;
import java.util.ArrayList;

import static com.example.codewarsplugin.config.StringConstants.*;
import static com.intellij.ui.jcef.JBCefClient.Properties.JS_QUERY_POOL_SIZE;

public class Store {

    private SidePanel sidePanel;
    private KataDirectory directory;
    private ArrayList<KataDirectory> directoryList = new ArrayList<>();
    private JBTabbedPane tabbedPane;
    private final TabManager manager;
    private final JBCefClient client = JBCefApp.getInstance().createClient();
    private final JBCefBrowser browser = new JBCefBrowserBuilder().setClient(client).setUrl(SIGN_IN_URL).build();
    private final Project project;


    //EDT
    public Store(SidePanel sidePanel, Project project) {
        this.sidePanel = sidePanel;
        client.setProperty(JS_QUERY_POOL_SIZE, 10);

        this.tabbedPane = new JBTabbedPane();
        sidePanel.add(tabbedPane, BorderLayout.CENTER);

        manager = new TabManager(this, project);
        manager.setupTabs();
        this.project = project;
        Runtime.getRuntime().addShutdownHook(new Thread(this::cleanup));
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


    private void cleanup() {
        CefCookieManager.getGlobalManager().deleteCookies(null, null);
        browser.dispose();
        client.dispose();
        manager.getDescriptionBrowser().dispose();
    }

    public void setDirectories(ArrayList<KataDirectory> directories) {
        this.directoryList = directories;
    }

    public ArrayList<KataDirectory> getDirectories() {
        return directoryList;
    }
}
