package com.example.codewarsplugin.state;

import com.example.codewarsplugin.components.KataSubmitPanel;
import com.example.codewarsplugin.models.kata.KataDirectory;
import com.example.codewarsplugin.services.katas.KataSetupService;
import com.example.codewarsplugin.services.katas.KataSetupServiceClient;
import com.example.codewarsplugin.services.cookies.CookieService;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.components.JBTabbedPane;
import com.intellij.ui.jcef.*;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.handler.CefLoadHandler;
import org.cef.handler.CefLoadHandlerAdapter;

import javax.swing.*;

import java.awt.*;

import static com.example.codewarsplugin.config.StringConstants.*;

public class TabManager implements KataSetupServiceClient {

    private final JBTabbedPane jbTabbedPane;
    private final Store store;
    private final Project project;
    private final ToolWindow toolWindow;
    private final JBCefBrowser browser;
    private final JBCefClient client;
    private String previousUrl = "";
    private KataSetupService setupService = new KataSetupService();
    private final JBCefBrowser descriptionBrowser = new JBCefBrowserBuilder().build();

    public TabManager(Store store, Project project, ToolWindow toolWindow) {
        this.jbTabbedPane = store.getTabbedPane();
        this.store = store;
        this.browser = store.getBrowser();
        this.client = store.getClient();
        this.project = project;
        this.toolWindow = toolWindow;
    }

    public void setupDashboard(){
        jbTabbedPane.addTab(DASHBOARD, browser.getComponent());
        JBCefJSQuery query = JBCefJSQuery.create((JBCefBrowserBase) browser);

        final CefLoadHandler handler = new CefLoadHandlerAdapter() {
            @Override
            public void onLoadEnd(CefBrowser browser, CefFrame frame, int httpStatusCode) {
                if(frame.getURL().equals(DASHBOARD_URL) && (previousUrl.equals(SIGN_IN_URL) || previousUrl.equals(""))){
                    SwingUtilities.invokeLater(setupTabs());
                } else if(frame.getURL().contains("train")){
                    System.out.println("train: " + browser.getURL());
                    final String url = browser.getURL();
                    setupKata(url);
                }
                previousUrl = browser.getURL();
            }
        };
        client.addLoadHandler(handler, browser.getCefBrowser());
        browser.loadURL(SIGN_IN_URL);
    }

    public Runnable setupTabs() {
        return () -> {
            CookieService.getCookies(browser);
            System.out.println("Run the main thread");
        };
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
        ApplicationManager.getApplication().invokeLater(() -> {
            WriteCommandAction.runWriteCommandAction(project, openFiles(directory));
        }, ModalityState.defaultModalityState());
        KataSubmitPanel submitPanel = new KataSubmitPanel(store);
        int index = getTabIndex(WORKSPACE);
        if (index == -1) {
            jbTabbedPane.addTab(WORKSPACE, submitPanel);
            jbTabbedPane.setSelectedIndex(jbTabbedPane.getTabCount() - 1);
        } else {
            jbTabbedPane.setComponentAt(index, submitPanel);
            jbTabbedPane.setSelectedIndex(index);
        }
    }

    @Override
    public void setupDescription() {

        JPanel descriptionPanel = new JPanel(new BorderLayout());
        var record = store.getDirectory().getRecord();

        descriptionBrowser.loadURL(record.getUrl() + "/" + record.getSelectedLanguage().toLowerCase());
        descriptionPanel.add(descriptionBrowser.getComponent(), BorderLayout.CENTER);

        int index = getTabIndex(DESCRIPTION);
        if (index == -1) {
            jbTabbedPane.addTab(DESCRIPTION, descriptionPanel);
        } else {
            jbTabbedPane.setComponentAt(index, descriptionPanel);
        }
    }

    private Runnable openFiles(KataDirectory directory) {
        return () -> {
            WriteCommandAction.runWriteCommandAction(project, () -> {
                OpenFileDescriptor descriptor = new OpenFileDescriptor(project, directory.getTestFile());
                FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
                fileEditorManager.openTextEditor(descriptor, false);
                descriptor = new OpenFileDescriptor(project, directory.getWorkFile());
                fileEditorManager.openTextEditor(descriptor, true);
            });
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
}
