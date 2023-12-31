package com.cw.codewarsplugin.state;

import com.cw.codewarsplugin.SidePanel;
import com.cw.codewarsplugin.components.KataSelectorPanel;
import com.cw.codewarsplugin.components.KataSubmitPanel;
import com.cw.codewarsplugin.components.OverlaySpinner;
import com.cw.codewarsplugin.models.kata.KataDirectory;
import com.cw.codewarsplugin.services.katas.KataSetupService;
import com.cw.codewarsplugin.services.katas.KataSetupServiceClient;
import com.cw.codewarsplugin.services.cookies.CookieService;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.IconLoader;
import com.intellij.ui.jcef.*;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.handler.CefLoadHandler;
import org.cef.handler.CefLoadHandlerAdapter;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.cw.codewarsplugin.config.StringConstants.*;

public class Manager implements KataSetupServiceClient {

    private final Store store;
    private final Project project;
    private final SidePanel sidePanel;
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cardPanel = new JPanel(cardLayout);
    private final JBCefBrowser browser;
    private final JBCefClient client;
    private String previousUrl = "";
    private final JPanel spinner = new OverlaySpinner();
    private final KataSetupService setupService = new KataSetupService();
    private final AtomicBoolean shouldFetchAndCreateFilesOnUrlLoad = new AtomicBoolean(true);
    private final AtomicBoolean shouldReloadUrl = new AtomicBoolean(false);
    private final JPanel browserPanel = new JPanel(new BorderLayout());
    private KataSubmitPanel submitPanel;
    private KataSelectorPanel selectorPanel;

    public Manager(Store store, Project project, SidePanel sidePanel) {
        this.sidePanel = sidePanel;
        this.store = store;
        this.browser = store.getBrowser();
        this.client = store.getClient();
        this.project = project;
        setupTabs();
    }

    public void setupTabs(){

        var browserComponent = browser.getComponent();
        browserComponent.setPreferredSize(new Dimension(5000, 5000));
        browserPanel.add(browserComponent, BorderLayout.CENTER);
        cardPanel.add(browserPanel, "browser");
        cardPanel.add(spinner, "spinner");
        sidePanel.add(cardPanel, BorderLayout.CENTER);
        final CefLoadHandler handler = getBrowserListener();
        client.addLoadHandler(handler, browser.getCefBrowser());
        sidePanel.revalidate();
        sidePanel.repaint();
    }

    public void reloadBrowser() {
        if (shouldReloadUrl.get() && browser.getCefBrowser().getURL().contains("train")) {
            browser.getCefBrowser().reload();
            shouldFetchAndCreateFilesOnUrlLoad.set(false);
            sidePanel.revalidate();
            sidePanel.repaint();
        }
        shouldReloadUrl.set(false);
    }

    public void refresh() {



        if (submitPanel != null && selectorPanel != null) {
            sidePanel.remove(submitPanel);
            sidePanel.remove(selectorPanel);
        }

        submitPanel = new KataSubmitPanel(store);
        selectorPanel = new KataSelectorPanel(store, this);



        sidePanel.add(selectorPanel, BorderLayout.SOUTH);
        sidePanel.add(submitPanel, BorderLayout.NORTH);

        if(store.getDirectory() != null && !browser.getCefBrowser().getURL().equals(store.getDirectory().getRecord().getWorkUrl())) {
            browser.loadURL(store.getDirectory().getRecord().getWorkUrl());
        }


        sidePanel.revalidate();
        sidePanel.repaint();
    }


    private CefLoadHandler getBrowserListener() {
        return new CefLoadHandlerAdapter() {
            @Override
            public void onLoadEnd(CefBrowser browser, CefFrame frame, int httpStatusCode) {
                if(frame.getURL().equals(SIGN_IN_URL)) {
                    ApplicationManager.getApplication().invokeLater(() -> {
                        sidePanel.removeAll();
                        setupTabs();
                    });

                } else if((frame.getURL().equals(DASHBOARD_URL) || frame.getURL().equals(SETUP_URL)) && ((previousUrl.equals(SIGN_IN_URL) || previousUrl.contains("github")) || previousUrl.equals(""))){
                    ApplicationManager.getApplication().invokeLater(() -> getCookies());
                } else if(browser.getURL().contains("train") && !browser.getURL().contains("trainer") && shouldFetchAndCreateFilesOnUrlLoad.get() && !previousUrl.equals(browser.getURL())) {
                    ApplicationManager.getApplication().invokeLater(() -> {
                        showOverlaySpinner(true);
                        sidePanel.revalidate();
                        sidePanel.repaint();
                    });
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
        refresh();
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
            sidePanel.revalidate();
            sidePanel.repaint();
        });
    }

    @Override
    public void loadKata(KataDirectory directory, boolean loadUrl) {

        store.setCurrentKataDirectory(directory);
        refresh();
        if(loadUrl && directory.getRecord().getWorkUrl() != null) {
            browser.loadURL(directory.getRecord().getWorkUrl());
        }
        WriteCommandAction.runWriteCommandAction(project, openFiles(directory));
    }
    public void reloadWorkspace() {
        refresh();
    }

    @Override
    public void loadWorkspace(KataDirectory directory, boolean loadUrl) {

        store.setCurrentKataDirectory(directory);
        WriteCommandAction.runWriteCommandAction(project, openFiles(directory));

        ApplicationManager.getApplication().invokeLater(() -> {

            showOverlaySpinner(false);
            refresh();
        });
    }

    public void showOverlaySpinner(boolean show) {
        cardLayout.show(cardPanel, show? "spinner" : "browser");
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

    public AtomicBoolean getShouldFetchAndCreateFilesOnUrlLoad() {
        return shouldFetchAndCreateFilesOnUrlLoad;
    }
    public AtomicBoolean getShouldReloadUrl() {
        return shouldReloadUrl;
    }

    public JBCefBrowser getBrowser() {
        return browser;
    }
}
