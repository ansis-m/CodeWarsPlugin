package com.example.codewarsplugin;

import com.example.codewarsplugin.services.login.LoginService;
import com.example.codewarsplugin.services.login.WebDriver;
import com.example.codewarsplugin.services.project.MyProjectManager;
import com.example.codewarsplugin.state.StateParams;
import com.example.codewarsplugin.state.SyncService;
import com.example.codewarsplugin.state.Store;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.jcef.JBCefBrowser;
import org.cef.network.CefCookieManager;

import javax.swing.*;
import java.awt.*;

public class SidePanel extends JPanel {
    private Store store;
    private StateParams stateParams;

    public SidePanel(Project project, ToolWindow toolWindow) {
        setLayout(new BorderLayout());
        MyProjectManager.init(project, toolWindow);
        initPlugin(project, toolWindow);
        store.getCurrentView().setup();
    }

    private void initPlugin(Project project, ToolWindow toolWindow) {
        store = new Store(this);
        stateParams = new StateParams();
        stateParams.setProject(project);
        stateParams.setToolWindow(toolWindow);
        stateParams.setSidePanel(this);
        stateParams.setStore(store);
        store.setCurrentView(LoginService.loginSuccess? store.getLogedInView() : store.getLoginView());
        SyncService.addParams(stateParams);
        Runtime.getRuntime().addShutdownHook(new Thread(this::cleanup));
    }

    private void cleanup() {
        CefCookieManager.getGlobalManager().deleteCookies(null, null);
        //WebDriver.quit();
    }

    public StateParams getStateParams() {
        return stateParams;
    }
}
