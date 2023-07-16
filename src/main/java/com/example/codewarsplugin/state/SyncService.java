package com.example.codewarsplugin.state;

import com.example.codewarsplugin.services.LoginService;
import com.example.codewarsplugin.services.UserService;
import com.example.codewarsplugin.services.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.util.ArrayList;
import java.util.List;

public class SyncService {

    private static ChromeDriver driver;
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
            var vars = p.getVars();
            vars.getLoginManager().stopSpinner();
            vars.getLoginView().cleanup();
            vars.getLogedInView().setup();
        }
    }

    public static void logout() {
        LoginService.logout();
        UserService.clearUser();
        WebDriver.logout();
        for(StateParams p : stateParamsList) {
            var vars = p.getVars();
            vars.getLogedInView().cleanup();
            vars.getLoginView().setup();
        }

    }

    public static void startLoginSpinner(){
        for(StateParams p : stateParamsList) {
            var vars = p.getVars();
            vars.getLoginManager().startSpinner();
        }

    }

    public static void stopLoginSpinner(){
        for(StateParams p : stateParamsList) {
            var vars = p.getVars();
            vars.getLoginManager().stopSpinner();
        }

    }

}
