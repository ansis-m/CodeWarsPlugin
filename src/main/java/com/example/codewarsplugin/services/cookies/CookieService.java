package com.example.codewarsplugin.services.cookies;

import com.intellij.ui.jcef.JBCefBrowser;
import com.intellij.ui.jcef.JBCefCookie;

import java.net.*;
import java.net.http.HttpClient;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class CookieService {

    private static List<JBCefCookie> allCookies = new ArrayList<>();
    private static String csrfToken;
    private static String sessionId;
    private static JBCefCookie csrfCookie;
    private static JBCefCookie sessionIdCookie;
    public static boolean loginSuccess = false;
    private static HttpClient httpClient;
    private static CookieManager cookieManager = new CookieManager();


    public static void getCookies(JBCefBrowser browser) {
        try {
            allCookies = browser.getJBCefCookieManager().getCookies("https://www.codewars.com", true).get();
            csrfCookie = allCookies.stream().filter(cookie -> cookie.getName().contains("CSRF-TOKEN")).findFirst().get();
            sessionIdCookie = allCookies.stream().filter(cookie -> cookie.getName().contains("session_id")).findFirst().get();
            csrfToken = csrfCookie.getValue();
            sessionId = sessionIdCookie.getValue();
            loginSuccess = true;
            initHttpClient();

        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
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

    public static String getCsrfToken() {
        return csrfToken;
    }

    public static String getSessionId() {
        return sessionId;
    }

    public static HttpClient getHttpClient() {
        return httpClient;
    }

}
