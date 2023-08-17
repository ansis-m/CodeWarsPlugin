package com.example.codewarsplugin.state;

import com.example.codewarsplugin.SidePanel;
import com.example.codewarsplugin.models.kata.KataDirectory;
import com.intellij.openapi.project.Project;
import com.intellij.ui.jcef.JBCefApp;
import com.intellij.ui.jcef.JBCefBrowser;
import com.intellij.ui.jcef.JBCefBrowserBuilder;
import com.intellij.ui.jcef.JBCefClient;

import static com.example.codewarsplugin.config.StringConstants.*;

public class Store {
    private KataDirectory directory;
    private final Manager manager;
    private final JBCefClient client = JBCefApp.getInstance().createClient();
    private final JBCefBrowser browser = new JBCefBrowserBuilder().setClient(client).setUrl(SIGN_IN_URL).build();
    private final Project project;

    public Store(SidePanel sidePanel, Project project) {
        manager = new Manager(this, project, sidePanel);
        this.project = project;
    }
    public void setCurrentKataDirectory(KataDirectory directory) {
        this.directory = directory;
    }
    public KataDirectory getDirectory() {
        return directory;
    }
    public Manager getManager() {
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
