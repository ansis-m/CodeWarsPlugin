package com.example.codewarsplugin.state;

import com.example.codewarsplugin.components.KataSelectorPanel;
import com.example.codewarsplugin.components.KataSubmitPanel;
import com.example.codewarsplugin.models.kata.KataDirectory;
import com.example.codewarsplugin.services.files.parse.KataDirectoryParser;
import com.example.codewarsplugin.services.katas.KataSetupService;
import com.example.codewarsplugin.services.katas.KataSetupServiceClient;
import com.example.codewarsplugin.services.cookies.CookieService;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBTabbedPane;
import com.intellij.ui.jcef.*;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.handler.CefLoadHandler;
import org.cef.handler.CefLoadHandlerAdapter;

import javax.swing.*;

import java.awt.*;
import java.util.ArrayList;

import static com.example.codewarsplugin.config.StringConstants.*;

public class TabManager implements KataSetupServiceClient {

    private final JBTabbedPane jbTabbedPane;
    private final Store store;
    private final Project project;
    private final JBCefBrowser browser;
    private final JBCefClient client;
    private String previousUrl = "";
    private KataSetupService setupService = new KataSetupService();
    private final JBCefBrowser descriptionBrowser = new JBCefBrowserBuilder().build();
    private KataDirectoryParser parser;

    public TabManager(Store store, Project project) {
        this.jbTabbedPane = store.getTabbedPane();
        this.store = store;
        this.browser = store.getBrowser();
        this.client = store.getClient();
        this.project = project;
        this.parser = new KataDirectoryParser(project);
    }

    public void setupTabs(){
        SwingUtilities.invokeLater(() -> {
            jbTabbedPane.addTab(DASHBOARD, browser.getComponent());
            final CefLoadHandler handler = getBrowserListener();
            client.addLoadHandler(handler, browser.getCefBrowser());
            browser.loadURL(SIGN_IN_URL);
        });
        loadProjectTab();
        addTabListeners();
    }

    private void addTabListeners() {
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

    private void loadProjectTab() {

        ArrayList<KataDirectory> directories = parser.getDirectoryList();
        store.setDirectories(directories);
        SwingUtilities.invokeLater(() -> {
            int index = getTabIndex(PROJECT);
            if (directories.size() > 0) {
                KataSelectorPanel selectorPanel = new KataSelectorPanel(store);
                if (index == -1) {
                    jbTabbedPane.insertTab(PROJECT, null, selectorPanel, "Select kata from the current intellij project!", 1);
                } else {
                    jbTabbedPane.setComponentAt(index, selectorPanel);
                }
            } else if (index != -1){
                jbTabbedPane.removeTabAt(index);
            }
        });
    }

    private CefLoadHandler getBrowserListener() {
        return new CefLoadHandlerAdapter() {
            @Override
            public void onLoadEnd(CefBrowser browser, CefFrame frame, int httpStatusCode) {
                if(frame.getURL().equals(DASHBOARD_URL) && (previousUrl.equals(SIGN_IN_URL) || previousUrl.equals(""))){
                    SwingUtilities.invokeLater(getCookies());
                } else if(frame.getURL().contains("train")){
                    final String url = browser.getURL();
                    setupKata(url);
                }
                previousUrl = browser.getURL();
            }
        };
    }

    private Runnable getCookies() {
        return () -> CookieService.getCookies(browser);
    }

    public void setupKata(String url){
        setupService.setup(url, project, this);
        SwingUtilities.invokeLater(() -> browser.loadURL(DASHBOARD_URL));
    }


    public Store getStore() {
        return store;
    }

    @Override
    public Project getProject() {
        return project;
    }

    @Override
    public void notifyDirectoryCreationFail(KataDirectory directory) {

    }

    @Override
    public void setupWorkspace(KataDirectory directory) {
        store.setCurrentKataDirectory(directory);
        loadProjectTab();
        ApplicationManager.getApplication().invokeLater(() -> {
            WriteCommandAction.runWriteCommandAction(project, openFiles(directory));
        }, ModalityState.defaultModalityState());
        SwingUtilities.invokeLater(() -> {
            KataSubmitPanel submitPanel = new KataSubmitPanel(store);
            int index = getTabIndex(WORKSPACE);
            if (index == -1) {
                jbTabbedPane.addTab(WORKSPACE, submitPanel);
                jbTabbedPane.setSelectedIndex(jbTabbedPane.getTabCount() - 1);
            } else {
                jbTabbedPane.setComponentAt(index, submitPanel);
                jbTabbedPane.setSelectedIndex(index);
            }
            jbTabbedPane.revalidate();
            jbTabbedPane.repaint();
        });

    }

    @Override
    public void setupDescription() {
        SwingUtilities.invokeLater(() -> {
            var record = store.getDirectory().getRecord();
            descriptionBrowser.loadURL(record.getUrl() + "/" + record.getSelectedLanguage().toLowerCase());
            int index = getTabIndex(DESCRIPTION);
            if (index == -1) {
                jbTabbedPane.addTab(DESCRIPTION, descriptionBrowser.getComponent());
            } else {
                jbTabbedPane.setComponentAt(index, descriptionBrowser.getComponent());
            }
        });
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
