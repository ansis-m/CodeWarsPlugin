package com.example.codewarsplugin.state;

import com.example.codewarsplugin.SidePanel;
import com.example.codewarsplugin.components.KataSelectorPanel;
import com.example.codewarsplugin.components.KataSubmitPanel;
import com.example.codewarsplugin.components.OverlaySpinner;
import com.example.codewarsplugin.models.kata.KataDirectory;
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
import java.awt.*;
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
    private final AtomicBoolean shouldFetchAndCreateFilesOnUrlLoad = new AtomicBoolean(true);
    private final AtomicBoolean shouldReloadUrl = new AtomicBoolean(false);
    private final JPanel browserPanel = new JPanel(new BorderLayout());
    private JPanel workPanel = new JPanel(new GridBagLayout());
    private KataSubmitPanel submitPanel = null;
    private final JLabel emptyWorkspace = new JLabel("Select Kata", IconLoader.getIcon(MESSAGE_ICON, SidePanel.class), JLabel.CENTER);

    public TabManager(Store store, Project project) {
        this.jbTabbedPane = store.getTabbedPane();
        this.store = store;
        this.browser = store.getBrowser();
        this.client = store.getClient();
        this.project = project;
        emptyWorkspace.setPreferredSize(new Dimension(5000, 5000));
    }

    public void setupTabs(){

        var browserComponent = browser.getComponent();
        browserComponent.setPreferredSize(new Dimension(5000, 5000));

        browserPanel.add(browserComponent, BorderLayout.CENTER);
        jbTabbedPane.insertTab(BROWSER, AllIcons.Javaee.WebService, browserPanel, "Search and select kata!", jbTabbedPane.getTabCount());
        final CefLoadHandler handler = getBrowserListener();
        client.addLoadHandler(handler, browser.getCefBrowser());
        browser.loadURL(SIGN_IN_URL);
        emptyWorkspace.setFont(emptyWorkspace.getFont().deriveFont(20f));
        loadAboutTab();
        addTabListeners();
    }

    // This is called from EDT on init
    private void loadAboutTab() {
        jbTabbedPane.insertTab(ABOUT, AllIcons.Actions.Help, new JLabel("to do"), "How to use the plugin?", jbTabbedPane.getTabCount());
        jbTabbedPane.revalidate();
        jbTabbedPane.repaint();
    }

    // This is called from EDT on init
    private void addTabListeners() {
        jbTabbedPane.addChangeListener(e -> {
            int selectedIndex = jbTabbedPane.getSelectedIndex();
            String title = jbTabbedPane.getTitleAt(selectedIndex);
            switch (title) {
                case (WORKSPACE): {
                    loadProjectTab();
                    break;
                }
                case (BROWSER): {
                    reloadBrowser();
                    break;
                }
            }
        });
    }

    private void reloadBrowser() {
        if (shouldReloadUrl.get() && browser.getCefBrowser().getURL().contains("train")) {
            browser.getCefBrowser().reload();
            shouldFetchAndCreateFilesOnUrlLoad.set(false);
            jbTabbedPane.revalidate();
            jbTabbedPane.repaint();
        }
        shouldReloadUrl.set(false);
    }

    private void loadProjectTab() {

        int index = getTabIndex(WORKSPACE);
        configureWorkPanel();

        if (index == -1) {
            jbTabbedPane.insertTab(WORKSPACE, AllIcons.Actions.MenuOpen, workPanel, "Select kata from the current intellij project!", 1);
        } else {
            jbTabbedPane.setComponentAt(index, workPanel);
        }
        jbTabbedPane.revalidate();
        jbTabbedPane.repaint();
    }

    private void configureWorkPanel() {
        workPanel = new JPanel();
        workPanel.setLayout(new GridBagLayout());
        KataSelectorPanel selectorPanel = new KataSelectorPanel(store, this);
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.weightx = 1;
        constraints.weighty = 0.25;
        constraints.gridx = 0;
        constraints.gridy = 0;
        workPanel.add(selectorPanel, constraints);
        constraints.gridy = 1;
        JPanel dummy = new JPanel();
        dummy.setPreferredSize(new Dimension(5000, 5000));
        workPanel.add(dummy, constraints);
        constraints.gridy = 2;
        workPanel.add(submitPanel == null? emptyWorkspace : submitPanel, constraints);
        constraints.gridy = 3;
        JPanel dummy2 = new JPanel();
        dummy2.setPreferredSize(new Dimension(5000, 5000));
        workPanel.add(dummy2, constraints);
    }

    private CefLoadHandler getBrowserListener() {
        return new CefLoadHandlerAdapter() {
            @Override
            public void onLoadEnd(CefBrowser browser, CefFrame frame, int httpStatusCode) {
                if((frame.getURL().equals(DASHBOARD_URL) || frame.getURL().equals(SETUP_URL)) && ((previousUrl.equals(SIGN_IN_URL) || previousUrl.contains("github")) || previousUrl.equals(""))){
                    ApplicationManager.getApplication().invokeLater(() -> getCookies());
                } else if(browser.getURL().contains("train") && !browser.getURL().contains("trainer") && shouldFetchAndCreateFilesOnUrlLoad.get() && !previousUrl.equals(browser.getURL())) {
                    ApplicationManager.getApplication().invokeLater(() -> showOverlaySpinner(true));
                    final String url = browser.getURL();
                    setupKata(url);
                }
                previousUrl = browser.getURL();
                shouldFetchAndCreateFilesOnUrlLoad.set(true);
                shouldReloadUrl.set(false);
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

    @Override
    public Project getProject() {
        return project;
    }

    @Override
    public void notifyKataFileCreationFail(String reason) {

        ApplicationManager.getApplication().invokeLater(() -> {
            this.showOverlaySpinner(false);
            browser.loadURL(DASHBOARD_URL);
            Messages.showMessageDialog(
                    reason,
                    "Ups, Something Went Wrong...",
                    IconLoader.getIcon(MESSAGE_ICON, SidePanel.class)
            );
            jbTabbedPane.revalidate();
            jbTabbedPane.repaint();
        });
    }

    @Override
    public void loadWorkspaceTab(KataDirectory directory, boolean loadUrl) {

        store.setCurrentKataDirectory(directory);
        WriteCommandAction.runWriteCommandAction(project, openFiles(directory));

        submitPanel = new KataSubmitPanel(store);
        if(loadUrl && directory.getRecord().getWorkUrl() != null) {
            int webIndex = getTabIndex(BROWSER);
            jbTabbedPane.setSelectedIndex(webIndex);
            browser.loadURL(directory.getRecord().getWorkUrl());
        }
        configureWorkPanel();
    }
    public void reloadWorkspace() {
        submitPanel = new KataSubmitPanel(store);
        configureWorkPanel();
        loadProjectTab();
    }

    @Override
    public void loadWorkspace(KataDirectory directory, boolean loadUrl) {

        store.setCurrentKataDirectory(directory);
        WriteCommandAction.runWriteCommandAction(project, openFiles(directory));

        ApplicationManager.getApplication().invokeLater(() -> {

            submitPanel = new KataSubmitPanel(store);
            configureWorkPanel();
            showOverlaySpinner(false);
            jbTabbedPane.revalidate();
            jbTabbedPane.repaint();
        });
    }

    public void showOverlaySpinner(boolean show) {

        int index = getTabIndex(BROWSER);
        if(index == -1) {
            return;
        }
        if (show) {
            jbTabbedPane.setComponentAt(index, spinner);
        } else {
            jbTabbedPane.setComponentAt(index, browserPanel);
        }
        jbTabbedPane.revalidate();
        jbTabbedPane.repaint();
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

    public AtomicBoolean getShouldFetchAndCreateFilesOnUrlLoad() {
        return shouldFetchAndCreateFilesOnUrlLoad;
    }
    public AtomicBoolean getShouldReloadUrl() {
        return shouldReloadUrl;
    }
}
