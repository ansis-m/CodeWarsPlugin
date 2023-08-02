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

    private final SidePanel sidePanel;
    private final JBTabbedPane jbTabbedPane;
    private final Store store;
    private final JBCefBrowser browser;
    private final JBCefClient client;

    public TabManager(SidePanel sidePanel, Store store) {
        this.jbTabbedPane = store.getTabbedPane();
        this.sidePanel = sidePanel;
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
            SwingUtilities.invokeLater(setupTabs());
            return null;
        });

        handlerHolder[0] = new CefLoadHandlerAdapter() {

            @Override
            public void onLoadEnd(CefBrowser browser, CefFrame frame, int httpStatusCode) {
                if(!frame.getURL().equals(DASHBOARD_URL)){
                    return;
                }
                System.out.println("inside listener");
                browser.executeJavaScript(SERIALIZE_WINDOW + query.inject("result"), browser.getURL(),0);
            }
        };
        client.addLoadHandler(handlerHolder[0], browser.getCefBrowser());
        browser.loadURL(SIGN_IN_URL);
    }

    public Runnable setupTabs() {
        return new Runnable() {
            @Override
            public void run() {

                LoginService.getCookies(browser);
                System.out.println("Run the main thread");
            }
        };
    }
}
