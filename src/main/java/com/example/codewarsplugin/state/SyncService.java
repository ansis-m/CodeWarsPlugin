package com.example.codewarsplugin.state;

import com.example.codewarsplugin.components.UserPanel;
import com.example.codewarsplugin.services.login.LoginService;
import com.example.codewarsplugin.services.UserService;
import com.example.codewarsplugin.services.login.WebDriver;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

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
            var vars = p.getStore();
            vars.getCurrentView().cleanup();
            vars.getPreviousViews().add(vars.getCurrentView());
            vars.setCurrentView(vars.getLogedInView());
            vars.getCurrentView().setup();
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
                    var vars = p.getStore();
                    vars.getCurrentView().cleanup();
                    vars.getPreviousViews().add(vars.getCurrentView());
                    vars.setCurrentView(vars.getLoginView());
                    vars.getCurrentView().setup();
                }
            }
        };
        worker.execute();
    }

    public static void startLoginSpinner(){
        for(StateParams p : stateParamsList) {
            var vars = p.getStore();
            vars.getLoginManager().startSpinner();
        }
    }

    public static void stopLoginSpinner(){
        for(StateParams p : stateParamsList) {
            var vars = p.getStore();
            vars.getLoginManager().stopSpinner();
        }
    }
}
