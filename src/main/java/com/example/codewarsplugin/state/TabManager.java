package com.example.codewarsplugin.state;

import com.example.codewarsplugin.SidePanel;
import com.example.codewarsplugin.components.KataSelectorPanel;
import com.example.codewarsplugin.components.KataSubmitPanel;
import com.example.codewarsplugin.components.OverlaySpinner;
import com.example.codewarsplugin.models.kata.KataDirectory;
import com.example.codewarsplugin.services.files.parse.KataDirectoryParser;
import com.example.codewarsplugin.services.katas.KataSetupService;
import com.example.codewarsplugin.services.katas.KataSetupServiceClient;
import com.example.codewarsplugin.services.cookies.CookieService;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.IconLoader;
import com.intellij.ui.components.JBTabbedPane;
import com.intellij.ui.jcef.*;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.handler.CefLoadHandler;
import org.cef.handler.CefLoadHandlerAdapter;

import javax.swing.*;


import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.example.codewarsplugin.config.StringConstants.*;

public class TabManager implements KataSetupServiceClient {

    private final JBTabbedPane jbTabbedPane;
    private final Store store;
    private final Project project;
    private final JBCefBrowser browser;
    private final JBCefClient client;
    private String previousUrl = "";
    private final JPanel spinner = new OverlaySpinner();
    private final KataSetupService setupService = new KataSetupService();
    private final JBCefBrowser descriptionBrowser = new JBCefBrowserBuilder().setUrl(BASE_URL).build();
    private final KataDirectoryParser parser;
    private final AtomicBoolean kataLoadIsInProgress = new AtomicBoolean(false);

    //EDT on initialization
    public TabManager(Store store, Project project) {
        this.jbTabbedPane = store.getTabbedPane();
        this.store = store;
        this.browser = store.getBrowser();
        this.client = store.getClient();
        this.project = project;
        this.parser = new KataDirectoryParser(project);
    }

    //EDT on initialization
    public void setupTabs(){

        jbTabbedPane.insertTab(DASHBOARD, AllIcons.Javaee.WebService, browser.getComponent(), "Search and select kata!", jbTabbedPane.getTabCount());
        final CefLoadHandler handler = getBrowserListener();
        client.addLoadHandler(handler, browser.getCefBrowser());
        browser.loadURL(SIGN_IN_URL);

        loadAboutTab();
        addTabListeners();
    }

    // This is called from EDT on init
    private void loadAboutTab() {
        jbTabbedPane.insertTab(ABOUT, AllIcons.Actions.Help, new JLabel("to do"), "How to use the plugin?", jbTabbedPane.getTabCount());
    }

    // This is called from EDT on init
    private void addTabListeners() {

        System.out.println("add listeners: " + SwingUtilities.isEventDispatchThread());
        jbTabbedPane.addChangeListener(e -> {
            int selectedIndex = jbTabbedPane.getSelectedIndex();
            String title = jbTabbedPane.getTitleAt(selectedIndex);
            switch (title) {
                case (PROJECT): {
                    loadProjectTab();
                    break;
                }
                default:
                    break;
            }
        });
    }


    //called from EDT on tab listener
    //called from EDT on login
    private void loadProjectTab() {

        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            ArrayList<KataDirectory> directories = parser.getDirectoryList();
            store.setDirectories(directories);

            ApplicationManager.getApplication().invokeLater(() -> {
                int index = getTabIndex(PROJECT);
                KataSelectorPanel selectorPanel = new KataSelectorPanel(store, this);
                if (index == -1) {
                    jbTabbedPane.insertTab(PROJECT, AllIcons.Actions.MenuOpen, selectorPanel, "Select kata from the current intellij project!", 1);
                } else {
                    jbTabbedPane.setComponentAt(index, selectorPanel);
                }
            });
        });
    }

    private CefLoadHandler getBrowserListener() {
        return new CefLoadHandlerAdapter() {

            @Override
            public void onLoadingStateChange(
                    CefBrowser browser, boolean isLoading, boolean canGoBack, boolean canGoForward) {

                if(browser.getURL().contains("train") && !kataLoadIsInProgress.get()) {
                    ApplicationManager.getApplication().invokeLater(() -> showOverlaySpinner(true));
                    kataLoadIsInProgress.set(true);
                    final String url = browser.getURL();
                    setupKata(url);
                }
            }

            @Override
            public void onLoadEnd(CefBrowser browser, CefFrame frame, int httpStatusCode) {
                if(frame.getURL().equals(DASHBOARD_URL) && (previousUrl.equals(SIGN_IN_URL) || previousUrl.equals(""))){
                    ApplicationManager.getApplication().invokeLater(() -> getCookies());
                }

                previousUrl = browser.getURL();
            }
        };
    }

    private void getCookies() {
        CookieService.getCookies(browser);
        loadProjectTab();
    }

    public void setupKata(String url){
        setupService.setup(url, project, this);

    }


    public Store getStore() {
        return store;
    }

    @Override
    public Project getProject() {
        return project;
    }

    //this comes from a side thread
    @Override
    public void notifyKataFileCreationFail(String reason) {
        kataLoadIsInProgress.set(false);
        ApplicationManager.getApplication().invokeLater(() -> {
            this.showOverlaySpinner(false);
            browser.loadURL(DASHBOARD_URL);
            Messages.showMessageDialog(
                    reason,
                    "Ups, Something Went Wrong...",
                    IconLoader.getIcon("/icons/new_cw_logo.svg", SidePanel.class)
            );
        });
    }


    /*
        ensure it is called from a proper side thread
     */

    @Override
    public void loadWorkspaceTab(KataDirectory directory) {

        store.setCurrentKataDirectory(directory);
        WriteCommandAction.runWriteCommandAction(project, openFiles(directory));

        ApplicationManager.getApplication().invokeLater(() -> {

            browser.loadURL(DASHBOARD_URL);
            KataSubmitPanel submitPanel = new KataSubmitPanel(store);
            int index = getTabIndex(WORKSPACE);
            int aboutIndex = getTabIndex(ABOUT);
            int dashboardIndex = getTabIndex(DASHBOARD);
            if (index == -1) {
                jbTabbedPane.insertTab(WORKSPACE, AllIcons.Actions.Commit, submitPanel, "Test and submit!", aboutIndex != -1? aboutIndex : jbTabbedPane.getTabCount());
            } else {
                jbTabbedPane.setComponentAt(index, submitPanel);
            }
            jbTabbedPane.setComponentAt(dashboardIndex, browser.getComponent());
            loadDescriptionTab();
            jbTabbedPane.setSelectedIndex(getTabIndex(WORKSPACE));
            kataLoadIsInProgress.set(false);
            showOverlaySpinner(false);
        });
    }

    public void showOverlaySpinner(boolean show) {

        int index = getTabIndex(DASHBOARD);
        if(index == -1) {
            return;
        }

        if (show) {
            jbTabbedPane.setComponentAt(index, spinner);
        } else {
            jbTabbedPane.setComponentAt(index, browser.getComponent());
        }
        jbTabbedPane.revalidate();
        jbTabbedPane.repaint();
    }


    //called only from EDT
    @Override
    public void loadDescriptionTab() {
            var record = store.getDirectory().getRecord();
            descriptionBrowser.loadURL(record.getUrl() + "/" + record.getSelectedLanguage().toLowerCase());
            int index = getTabIndex(DESCRIPTION);
            int workspaceIndex = getTabIndex(WORKSPACE);
            if (index == -1) {
                jbTabbedPane.insertTab(DESCRIPTION, AllIcons.Actions.Find, descriptionBrowser.getComponent(), "Kata description in a browser window!", workspaceIndex > 0 ? workspaceIndex + 1 : jbTabbedPane.getTabCount());
            } else {
                jbTabbedPane.setComponentAt(index, descriptionBrowser.getComponent());
            }
    }

    private Runnable openFiles(KataDirectory directory) {
        return () -> {
                OpenFileDescriptor descriptor = new OpenFileDescriptor(project, directory.getTestFile());
                FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
                fileEditorManager.openTextEditor(descriptor, false);
                descriptor = new OpenFileDescriptor(project, directory.getWorkFile());
                fileEditorManager.openTextEditor(descriptor, true);
        };
    }


    private int getTabIndex(String title) {

        for(int i = 0; i < jbTabbedPane.getTabCount(); i++) {
            if (jbTabbedPane.getTitleAt(i).equals(title)) {
                return i;
            }
        }
        return -1;
    }

    public JBCefBrowser getDescriptionBrowser() {
        return descriptionBrowser;
    }

}
