package com.example.codewarsplugin.services.login;

import com.example.codewarsplugin.services.UserService;
import com.example.codewarsplugin.state.SyncService;
import com.intellij.ui.jcef.JBCefBrowserBase;
import com.intellij.ui.jcef.JBCefCookie;
import com.intellij.ui.jcef.JBCefJSQuery;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.handler.CefLoadHandler;
import org.cef.handler.CefLoadHandlerAdapter;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.net.*;
import java.net.http.HttpClient;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.example.codewarsplugin.CodewarsToolWindowFactory.browser;
import static com.example.codewarsplugin.CodewarsToolWindowFactory.client;
import static com.example.codewarsplugin.config.StringConstants.DASHBOARD_URL;


public class LoginService {

    private static @NotNull List<JBCefCookie> allCookies = new ArrayList<>();
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
                csrfCookie = allCookies.stream().filter(cookie -> cookie.getName().contains("CSRF-TOKEN")).findFirst().get();
                sessionIdCookie = allCookies.stream().filter(cookie -> cookie.getName().contains("session_id")).findFirst().get();
                csrfToken = csrfCookie.getValue();
                sessionId = sessionIdCookie.getValue();
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

    public static String getCsrfToken() {
        return csrfToken;
    }

    public static String getSessionId() {
        return sessionId;
    }

    public static HttpClient getHttpClient() {
        return httpClient;
    }

    public static void logout() {
        allCookies = new ArrayList<>();
        csrfToken = null;
        sessionId = null;
        loginSuccess = false;


        JBCefJSQuery query = JBCefJSQuery.create((JBCefBrowserBase) browser);
        query.addHandler(result -> {
            System.out.println("result: " + result );
            //SwingUtilities.invokeLater(LoginService.getCookies());
            return null;
        });


        final CefLoadHandler[] handlerHolder = new CefLoadHandler[1];
        handlerHolder[0] = new CefLoadHandlerAdapter() {

            @Override
            public void onLoadEnd(CefBrowser browser, CefFrame frame, int httpStatusCode) {
                if(!frame.getURL().equals("https://www.codewars.com/kumite")){
                    return;
                }
                System.out.println("inside listener");
                //ApplicationManager.getApplication().invokeLater(() -> client.removeLoadHandler(handlerHolder[0], browser));
                browser.executeJavaScript(
                        "var result = document.querySelector('meta[name=\"csrf-token\"]').content; "
                                + query.inject("result"),

                        browser.getURL(),
                        0
                );
            }
        };

        client.addLoadHandler(handlerHolder[0], browser.getCefBrowser());



        browser.loadURL("https://www.codewars.com/kumite");
        // Create a JS query
//        JBCefJSQuery jsQuery = JBCefJSQuery.create((JBCefBrowserBase) browser);
//        jsQuery.addHandler(result -> {
//            System.out.println("result: " + result);
//            return null;
//        });
//
//
//        String jsCode = "var result = document.querySelector('meta[name=\"csrf-token\"]').content; ";
//
//        browser.getCefBrowser().executeJavaScript(
//            jsCode +
//                    jsQuery.inject("result"),
//
//                browser.getCefBrowser().getURL(),
//                0
//        );


    }
}
