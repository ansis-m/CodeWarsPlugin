package com.example.codewarsplugin.state;

import com.example.codewarsplugin.models.kata.KataRecord;
import com.example.codewarsplugin.services.UserService;
import com.example.codewarsplugin.services.katas.KataRecordService;
import com.example.codewarsplugin.services.katas.KataSetupService;
import com.example.codewarsplugin.services.katas.KataSetupServiceClient;
import com.example.codewarsplugin.services.login.LoginService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
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

public class TabManager implements KataSetupServiceClient {

    private final JBTabbedPane jbTabbedPane;
    private final Store store;
    private final Project project;
    private final ToolWindow toolWindow;
    private final JBCefBrowser browser;
    private final JBCefClient client;
    private String previousUrl = "";
    private KataSetupService setupService = new KataSetupService();

    public TabManager(Store store, Project project, ToolWindow toolWindow) {
        this.jbTabbedPane = store.getTabbedPane();
        this.store = store;
        this.browser = store.getBrowser();
        this.client = store.getClient();
        this.project = project;
        this.toolWindow = toolWindow;
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
                    SwingUtilities.invokeLater(setupTabs());
                } else if(frame.getURL().contains("train")){
                    System.out.println("train: " + browser.getURL());
                    final String url = browser.getURL();
                    setupKata(url);
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

    public void setupKata(String url){
        setupService.setup(url, this);
        SwingUtilities.invokeLater(() -> browser.loadURL(DASHBOARD_URL));
    }


    public Store getStore() {
        return store;
    }

    @Override
    public Project getProject() {
        return project;
    }
}
