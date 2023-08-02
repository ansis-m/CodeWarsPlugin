package com.example.codewarsplugin.services.login;

import com.example.codewarsplugin.state.SyncService;
import com.intellij.ui.jcef.JBCefCookie;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.chrome.ChromeDriver;


import java.net.*;
import java.net.http.HttpClient;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.example.codewarsplugin.CodewarsToolWindowFactory.browser;


public class LoginService {

    private static @NotNull List<JBCefCookie> allCookies = new ArrayList<>();
    private static ChromeDriver driver;
    private static String currentLogin;
    private static String currentPassword;
    private static String csrfToken;
    private static String sessionId;
    private static JBCefCookie csrfCookie;
    private static JBCefCookie sessionIdCookie;
    public static boolean loginSuccess = false;
    private static HttpClient httpClient;
    private static CookieManager cookieManager = new CookieManager();;


    public static Runnable getCookies() {

        return () -> {

            try {
                allCookies = browser.getJBCefCookieManager().getCookies("https://www.codewars.com", true).get();
                allCookies.forEach(c -> System.out.println("name: " + c.getName() + " " + c.getValue()));
                csrfCookie = allCookies.stream().filter(cookie -> cookie.getName().contains("CSRF-TOKEN")).findFirst().get();
                sessionIdCookie = allCookies.stream().filter(cookie -> cookie.getName().contains("session_id")).findFirst().get();
                csrfToken = csrfCookie.getValue();
                sessionId = sessionIdCookie.getValue();

                System.out.println("csrf token: " + csrfToken);
                System.out.println("session id: " + sessionId);


                loginSuccess = true;
                initHttpClient();
                SyncService.login();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }

        };
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
        allCookies = new ArrayList<>();
        currentLogin = null;
        currentPassword = null;
        csrfToken = null;
        sessionId = null;
        loginSuccess = false;
    }
}
