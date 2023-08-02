package com.example.codewarsplugin.views;


import com.example.codewarsplugin.SidePanel;
import com.example.codewarsplugin.services.UserService;
import com.example.codewarsplugin.services.login.LoginService;
import com.example.codewarsplugin.state.Store;
import com.intellij.ui.jcef.JBCefBrowserBase;
import com.intellij.ui.jcef.JBCefJSQuery;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.handler.CefLoadHandler;
import org.cef.handler.CefLoadHandlerAdapter;

import javax.swing.*;
import java.awt.*;

import static com.example.codewarsplugin.CodewarsToolWindowFactory.browser;
import static com.example.codewarsplugin.CodewarsToolWindowFactory.client;
import static com.example.codewarsplugin.config.StringConstants.DASHBOARD_URL;


public class LoginView implements View{

    private Store store;
    private SidePanel sidePanel;


    public LoginView(Store store) {
        this.store = store;
        this.sidePanel = store.getSidePanel();
    }

    public void setup() {

        sidePanel.add(browser.getComponent(), BorderLayout.CENTER);
        sidePanel.revalidate();
        sidePanel.repaint();


        final CefLoadHandler[] handlerHolder = new CefLoadHandler[1];

        JBCefJSQuery query = JBCefJSQuery.create((JBCefBrowserBase) browser);
        query.addHandler(result -> {
            System.out.println("\n\n\nuser: " + UserService.getUser(result));
            SwingUtilities.invokeLater(LoginService.getCookies());
            return null;
        });

        handlerHolder[0] = new CefLoadHandlerAdapter() {

            @Override
            public void onLoadEnd(CefBrowser browser, CefFrame frame, int httpStatusCode) {
                if(!frame.getURL().equals(DASHBOARD_URL)){
                    return;
                }
                System.out.println("inside listener");
                //ApplicationManager.getApplication().invokeLater(() -> client.removeLoadHandler(handlerHolder[0], browser));
                browser.executeJavaScript(
                        "function serialize(obj) {" +
                                "  const seen = new WeakSet();" +
                                "  return JSON.stringify(obj, (key, value) => {" +
                                "    if (typeof value === 'object' && value !== null) {" +
                                "      if (seen.has(value)) {" +
                                "        return '[Circular Reference]';" +
                                "      }" +
                                "      seen.add(value);" +
                                "    }" +
                                "    return value;" +
                                "  });" +
                                "}" +
                                "var result = serialize(window);"
                        + query.inject("result"),

                        browser.getURL(),
                        0
                );
            }

            @Override
            public void onLoadingStateChange(
                    CefBrowser browser, boolean isLoading, boolean canGoBack, boolean canGoForward) {

                System.out.println("loading state change main frame url: " + browser.getMainFrame().getURL());


            }
        };

        client.addLoadHandler(handlerHolder[0], browser.getCefBrowser());
    }


    public void cleanup() {
        sidePanel.removeAll();
    }

    @Override
    public void refreshUserPanel() {

    }
}
