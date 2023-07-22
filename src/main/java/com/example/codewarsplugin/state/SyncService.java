package com.example.codewarsplugin.state;

import com.example.codewarsplugin.services.login.LoginService;
import com.example.codewarsplugin.services.UserService;
import com.example.codewarsplugin.services.login.WebDriver;

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
        LoginService.logout();
        UserService.clearUser();
        WebDriver.logout();
        for(StateParams p : stateParamsList) {
            var vars = p.getStore();
            vars.getCurrentView().cleanup();
            vars.getPreviousViews().add(vars.getCurrentView());
            vars.setCurrentView(vars.getLoginView());
            vars.getCurrentView().setup();
        }

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
