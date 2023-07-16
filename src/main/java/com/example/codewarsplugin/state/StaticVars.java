package com.example.codewarsplugin.state;

import org.openqa.selenium.chrome.ChromeDriver;

import java.util.ArrayList;
import java.util.List;

public class StaticVars {

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
            vars.getLoginView().cleanUp();
            vars.getLogedInView().setup();


        }



    }

}
