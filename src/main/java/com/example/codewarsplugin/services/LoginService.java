package com.example.codewarsplugin.services;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;


import javax.swing.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class LoginService {

    private static LoginService loginService = new LoginService();
    private static Set<Cookie> allCookies = new HashSet<>();
    private static ChromeDriver driver;
    private static String currentLogin;
    private static String currentPassword;
    private static String csrfToken;
    private static String sessionId;

    private LoginService(){
    }

    public static LoginService getInstance(){
        if (loginService == null){
            loginService = new LoginService();
        }
        return loginService;
    }

    public static boolean login(String login, String password){
        if(notValid(login, password) || !getCookies(login, password)){
            return false;
        }
        currentLogin = login;
        currentPassword = password;

        return true;
    }

    private static boolean getCookies(String login, String password) {

        System.out.println("getCookies");

        try{
            SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws InterruptedException {



                    driver = WebDriver.getChromeDriver();
                    driver.get("https://www.codewars.com/users/sign_in");
                    WebElement inputElement = driver.findElement(By.id("user_email"));
                    inputElement.clear();
                    inputElement.sendKeys("maleckisansis@gmail.com");
                    inputElement = driver.findElement(By.id("user_password"));
                    inputElement.clear();
                    inputElement.sendKeys("Eloraps1!");
                    WebElement buttonElement = driver.findElement(By.className("btn"));
                    buttonElement.click();
                    Thread.sleep(2000);
                    allCookies = driver.manage().getCookies();
                    csrfToken = allCookies.stream().filter(cookie -> cookie.getName().contains("CSRF-TOKEN")).findFirst().get().getValue();
                    sessionId = allCookies.stream().filter(cookie -> cookie.getName().contains("session_id")).findFirst().get().getValue();
                    currentPassword = password;
                    currentLogin = login;
                    return null;
                }

                @Override
                protected void done() {
                    SwingUtilities.invokeLater(() -> {
                        System.out.println("Invoke Later!!! " + csrfToken + "\nid: " + sessionId);
                    });
                }
            };
            worker.execute();
            return true;
        } catch (Exception e){
            System.out.println("Exception trying to login: " + e.getMessage());
            return false;
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

    private static boolean notValid(String login, String password) {
        return !(login != null && password != null && login.length() > 0 && password.length() > 0);
    }
    public static String getCurrentLogin() {
        return currentLogin;
    }

    public static String getCurrentPassword() {
        return currentPassword;
    }
}
