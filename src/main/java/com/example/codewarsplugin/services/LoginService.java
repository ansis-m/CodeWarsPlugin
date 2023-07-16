package com.example.codewarsplugin.services;

import com.example.codewarsplugin.state.SyncService;
import com.example.codewarsplugin.state.Vars;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;


import javax.swing.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class LoginService {

    private static Set<Cookie> allCookies = new HashSet<>();
    private static ChromeDriver driver;
    private static String currentLogin;
    private static String currentPassword;
    private static String csrfToken;
    private static String sessionId;
    public static boolean loginSuccess = false;

    private LoginService(){
    }


    public static void login(String login, String password, Vars vars){
        if(valid(login, password)){
            getCookies(login, password, vars);
        } else {
            vars.getLoginManager().stopSpinner();
            vars.getLoginManager().showLoginFailLabel("Enter a valid login and password to sign in!");
        }
    }

    private static void getCookies(String login, String password, Vars vars) {

        System.out.println("getCookies");

        if (loginSuccess) {
            vars.getLoginManager().stopSpinner();
            vars.getLoginView().cleanUp();
            vars.getLogedInView().setup();
            return;
        }

        try{
            SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws InterruptedException {

                    driver = WebDriver.getChromeDriver();
                    if (!driver.getCurrentUrl().equals("https://www.codewars.com/users/sign_in")){
                        driver.get("https://www.codewars.com/users/sign_in");
                    }
                    WebElement inputElement = driver.findElement(By.id("user_email"));
                    inputElement.clear();
                    inputElement.sendKeys("maleckisansis@gmail.com");
                    inputElement = driver.findElement(By.id("user_password"));
                    inputElement.clear();
                    inputElement.sendKeys(password);
                    WebElement buttonElement = driver.findElement(By.className("btn"));
                    buttonElement.click();
                    Thread.sleep(2000);
                    if (!driver.getCurrentUrl().contains("codewars.com/dashboard")){
                        loginSuccess = false;
                        return null;
                    }
                    loginSuccess = true;
                    String[] arg = {};
                    //laikam vajadzetu turpinat ar draiveri, bet pagaidam paliek
                    allCookies = driver.manage().getCookies();
                    csrfToken = allCookies.stream().filter(cookie -> cookie.getName().contains("CSRF-TOKEN")).findFirst().get().getValue();
                    sessionId = allCookies.stream().filter(cookie -> cookie.getName().contains("session_id")).findFirst().get().getValue();
                    currentPassword = password;
                    currentLogin = login;
                    return null;
                }

                @Override
                protected void done() {
                    if (!loginSuccess) {
                        vars.getLoginManager().stopSpinner();
                        vars.getLoginManager().showLoginFailLabel("Login failed. Bad email or password!");
                    } else {
                        SyncService.login();
                    }
                }
            };
            worker.execute();
        } catch (Exception e){
            vars.getLoginManager().stopSpinner();
            System.out.println("Exception trying to login: " + e.getMessage());
        }
    }

    private static String extractToken(List<String> cookies, String id) {
        return cookies.stream()
                .filter(cookie -> cookie.contains(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(id + " not found in set_Cookie!"))
                .split(";")[0].split("=")[1];
    }

    public static String getCsrfToken() {
        return csrfToken;
    }

    public static String getSessionId() {
        return sessionId;
    }

    private static boolean valid(String login, String password) {

        System.out.println("login and password: " + login + " " + password);
        return login != null && password != null && login.length() > 0 && password.length() > 0 && !login.equals("email") && !password.equals("password");
    }
    public static String getCurrentLogin() {
        return currentLogin;
    }

    public static String getCurrentPassword() {
        return currentPassword;
    }

    public static void logout() {
        allCookies = new HashSet<>();
        currentLogin = null;
        currentPassword = null;
        csrfToken = null;
        sessionId = null;
        loginSuccess = false;
    }
}
