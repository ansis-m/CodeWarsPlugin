package com.example.codewarsplugin.views;

import com.example.codewarsplugin.CodewarsToolWindowFactory;
import com.example.codewarsplugin.SidePanel;
import com.example.codewarsplugin.components.LoginPanel;
import com.example.codewarsplugin.components.TopImagePanel;
import com.example.codewarsplugin.services.login.LoginService;
import com.example.codewarsplugin.state.Store;
import com.example.codewarsplugin.state.SyncService;
import com.google.type.DateTime;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.ui.jcef.JBCefBrowser;
import org.cef.browser.CefBrowser;
import org.cef.handler.CefLoadHandler;
import org.cef.handler.CefLoadHandlerAdapter;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.concurrent.ExecutionException;

import static com.example.codewarsplugin.CodewarsToolWindowFactory.browser;
import static com.example.codewarsplugin.CodewarsToolWindowFactory.client;
import static com.example.codewarsplugin.config.StringConstants.DASHBOARD_URL;
import static com.example.codewarsplugin.config.StringConstants.SIGN_IN_URL;


public class LoginView implements View{

    private Store store;
    private SidePanel sidePanel;
    private LoginPanel loginPanel;


    public LoginView(Store store) {
        this.store = store;
        this.sidePanel = store.getSidePanel();
    }

    public void setup() {

        sidePanel.add(browser.getComponent(), BorderLayout.CENTER);
        sidePanel.revalidate();
        sidePanel.repaint();


        CefLoadHandler[] handlerHolder = new CefLoadHandler[1];

        handlerHolder[0] = new CefLoadHandlerAdapter() {
            @Override
            public void onLoadingStateChange(
                    CefBrowser browser, boolean isLoading, boolean canGoBack, boolean canGoForward) {

                System.out.println("main frame url: " + browser.getMainFrame().getURL());

                if (isLoading){
                    System.out.println("Loading: " + browser.getURL() +  LocalDateTime.now());
                }
                if (!isLoading && browser.getURL().equals(DASHBOARD_URL)){
                    System.out.println("finished loading: " + browser.getURL() + LocalDateTime.now());
                    ApplicationManager.getApplication().invokeLater(() -> {
                        client.removeLoadHandler(handlerHolder[0], browser);
                    });
                    SwingUtilities.invokeLater(SyncService::login);
                }
            }};



        client.addLoadHandler(handlerHolder[0], browser.getCefBrowser());

    }

    public void cleanup() {
        //loginPanel.stopSpinner();
        sidePanel.removeAll();
    }

    @Override
    public void refreshUserPanel() {

    }
}
