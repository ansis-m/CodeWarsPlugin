package com.example.codewarsplugin.services;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class LoginService {


    private static String currentLogin;
    private static String currentPassword;
    private static String csrfToken;
    private static String authenticityToken;
    private static String sessionId;
    private static final RestTemplate restTemplate = new RestTemplate();



    public LoginService() {
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
    }

    public static boolean login(String login, String password){
        if(notValid(login, password) || !getCookies()){
            return false;
        }
        currentLogin = login;
        currentPassword = password;

        return true;
    }

    private static boolean notValid(String login, String password) {
        return !(login != null && password != null && login.length() > 0 && password.length() > 0);
    }

    private static boolean getCookies() {

        try{
            CookieService.login();
            CookieService.getAllCookies().forEach(c -> System.out.println(c.getName() + " ~~~~ " + c.getValue()));

            return true;
        } catch (Exception e){
            System.out.println("Exception while getting cookies: " + e.getMessage());
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

    public static String getAuthenticityToken() {
        return authenticityToken;
    }

    public static String getSessionId() {
        return sessionId;
    }
}
