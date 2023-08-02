package com.example.codewarsplugin.state;

import com.example.codewarsplugin.SidePanel;
import com.example.codewarsplugin.services.UserService;
import com.example.codewarsplugin.services.login.LoginService;
import com.intellij.ui.components.JBTabbedPane;
import com.intellij.ui.jcef.JBCefBrowser;
import com.intellij.ui.jcef.JBCefBrowserBase;
import com.intellij.ui.jcef.JBCefClient;
import com.intellij.ui.jcef.JBCefJSQuery;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.handler.CefLoadHandler;
import org.cef.handler.CefLoadHandlerAdapter;

import javax.swing.*;

import static com.example.codewarsplugin.config.StringConstants.*;

public class TabManager {

    private final JBTabbedPane jbTabbedPane;
    private final Store store;
    private final JBCefBrowser browser;
    private final JBCefClient client;
    private String previousUrl = "";

    public TabManager(Store store) {
        this.jbTabbedPane = store.getTabbedPane();
        this.store = store;
        this.browser = store.getBrowser();
        this.client = store.getClient();
    }

    public void setupDashboard(){
        jbTabbedPane.addTab("dashboard", browser.getComponent());

        final CefLoadHandler[] handlerHolder = new CefLoadHandler[1];

        JBCefJSQuery query = JBCefJSQuery.create((JBCefBrowserBase) browser);

        query.addHandler(result -> {
            System.out.println(result+ "\n\n\nuser: " + UserService.getUser(result) );
            return null;
        });

        handlerHolder[0] = new CefLoadHandlerAdapter() {

            @Override
            public void onLoadingStateChange(
                    CefBrowser browser, boolean isLoading, boolean canGoBack, boolean canGoForward) {
                System.out.println("change: " + isLoading + " url " + browser.getURL());
            }

            @Override
            public void onLoadEnd(CefBrowser browser, CefFrame frame, int httpStatusCode) {
                if(frame.getURL().equals(DASHBOARD_URL) && (previousUrl.equals(SIGN_IN_URL) || previousUrl.equals(""))){
                    System.out.println("inside listener");
                    browser.executeJavaScript(SERIALIZE_WINDOW + query.inject("result"), browser.getURL(),0);
                    SwingUtilities.invokeLater(setupTabs());
                } else if(frame.getURL().contains("train")){
                    System.out.println("train: " + browser.getURL());
                    final String url = browser.getURL();
                    SwingUtilities.invokeLater(setupKata(url));
                }
                previousUrl = browser.getURL();
            }
        };
        client.addLoadHandler(handlerHolder[0], browser.getCefBrowser());
        browser.loadURL(SIGN_IN_URL);
    }

    public Runnable setupTabs() {
        return () -> {
            LoginService.getCookies(browser);
            System.out.println("Run the main thread");
        };
    }

    public Runnable setupKata(String url){
        return () -> {
          System.out.println("setup kata: " + url);
          browser.loadURL(DASHBOARD_URL);
        };
    }
}
