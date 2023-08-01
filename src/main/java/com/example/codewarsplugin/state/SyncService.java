package com.example.codewarsplugin.state;

import com.example.codewarsplugin.services.login.LoginService;
import com.example.codewarsplugin.services.UserService;
import com.example.codewarsplugin.services.login.WebDriver;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.example.codewarsplugin.CodewarsToolWindowFactory.browser;

public class SyncService {
    private static List<StateParams> stateParamsList = new ArrayList();

    public static List<StateParams> getStateParamsList() {
        return stateParamsList;
    }

    public static void addParams(StateParams stateParams) {
        stateParamsList.add(stateParams);
    }

    public static void remove(StateParams stateParams) {
        stateParamsList.remove(stateParams);
    }

    public static void login(){
        for(StateParams p : stateParamsList) {
            var store = p.getStore();
            store.getCurrentView().cleanup();
            store.getPreviousViews().add(store.getCurrentView());
            store.setCurrentView(store.getLogedInView());
            store.getCurrentView().setup();
        }
        try {
            browser.getJBCefCookieManager().getCookies("https://www.codewars.com", false).get().forEach(c -> System.out.println("name: " + c.getName() + c.getValue()));
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public static void logout() {
        SwingWorker<Void, Void> worker = new SwingWorker<>(){
            @Override
            protected Void doInBackground() {
                LoginService.logout();
                UserService.clearUser();
                WebDriver.logout();
                return null;
            }
            @Override
            protected void done(){
                for(StateParams p : stateParamsList) {
                    var store = p.getStore();
                    store.getCurrentView().cleanup();
                    store.getPreviousViews().add(store.getCurrentView());
                    store.setCurrentView(store.getLoginView());
                    store.getCurrentView().setup();
                }
            }
        };
        worker.execute();
    }

    public static void startLoginSpinner(){
        for(StateParams p : stateParamsList) {
            var store = p.getStore();
            store.getLoginPanel().startSpinner();
        }
    }

    public static void stopLoginSpinner(){
        for(StateParams p : stateParamsList) {
            var store = p.getStore();
            store.getLoginPanel().stopSpinner();
        }
    }
}
