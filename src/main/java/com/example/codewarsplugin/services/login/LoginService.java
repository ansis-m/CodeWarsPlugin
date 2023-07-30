package com.example.codewarsplugin.services.login;

import com.example.codewarsplugin.state.SyncService;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;


import javax.swing.*;
import java.net.*;
import java.net.http.HttpClient;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.example.codewarsplugin.StringConstants.SIGN_IN_URL;


public class LoginService {

    private static Set<Cookie> allCookies = new HashSet<>();
    private static ChromeDriver driver;
    private static String currentLogin;
    private static String currentPassword;
    private static String csrfToken;
    private static String sessionId;
    private static Cookie csrfCookie;
    private static Cookie sessionIdCookie;
    public static boolean loginSuccess = false;
    private static HttpClient httpClient;
    private static CookieManager cookieManager = new CookieManager();;

    public static void login(String login, String password, LoginServiceClient client){
        if(valid(login, password)){
            getCookies(login, password, client);
        } else {
            SyncService.stopLoginSpinner();
            client.showLoginFailLabel("Enter a valid login and password to sign in!");
        }
    }

    private static void getCookies(String login, String password, LoginServiceClient client) {

        System.out.println("getCookies");
        try{
            SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws InterruptedException {

                    driver = WebDriver.getChromeDriver();
                    if (!driver.getCurrentUrl().equals(SIGN_IN_URL)){
                        driver.get(SIGN_IN_URL);
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
                    allCookies = driver.manage().getCookies();
                    csrfCookie = allCookies.stream().filter(cookie -> cookie.getName().contains("CSRF-TOKEN")).findFirst().get();
                    sessionIdCookie = allCookies.stream().filter(cookie -> cookie.getName().contains("session_id")).findFirst().get();
                    csrfToken = csrfCookie.getValue();
                    sessionId = sessionIdCookie.getValue();
                    currentPassword = password;
                    currentLogin = login;
                    return null;
                }

                @Override
                protected void done() {
                    if (!loginSuccess) {
                        SyncService.stopLoginSpinner();
                        client.showLoginFailLabel("Login failed. Bad email or password!");
                    } else {
                        initHttpClient();
                        SyncService.login();
                    }
                }
            };
            worker.execute();
        } catch (Exception e){
            client.showLoginFailLabel("Login failed. Bad email or password!");
            SyncService.stopLoginSpinner();
            System.out.println("Exception trying to login: " + e.getMessage());
        }
    }

    private static void initHttpClient() {
        cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);
        URI uri = URI.create("https://www.codewars.com");
        cookieManager.getCookieStore().add(uri, new HttpCookie(csrfCookie.getName(), csrfCookie.getValue()));
        cookieManager.getCookieStore().add(uri, new HttpCookie(sessionIdCookie.getName(), sessionIdCookie.getValue()));
        httpClient = HttpClient.newBuilder()
                .cookieHandler(cookieManager)
                .build();
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

    public static HttpClient getHttpClient() {
        return httpClient;
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
