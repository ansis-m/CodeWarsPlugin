package com.example.codewarsplugin.services;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class LoginService {


    private static String currentLogin;
    private static String currentPassword;
    private static String csrfToken;
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
            ResponseEntity<String> response = restTemplate.exchange("https://www.codewars.com/users/sign_in", HttpMethod.GET, null, String.class);
            System.out.println("response status: " + response.getStatusCodeValue());
            List<String> setCookie = response.getHeaders().get("set-cookie");
            csrfToken = URLDecoder.decode(extractToken(setCookie, "CSRF-TOKEN"), StandardCharsets.UTF_8);
            System.out.println("csrf Token: " + csrfToken);
            sessionId = extractToken(setCookie, "session_id");
            System.out.println("sessionId: " + sessionId);
            return true;
        } catch (Exception e){
            System.out.println("Exception while getting cookies: " + e.getMessage());
            return false;
        }


//        if (!CollectionUtils.isEmpty(cookies) && csrfToken != null) {
//            HttpHeaders requestHeaders = new HttpHeaders();
//            requestHeaders.put(HttpHeaders.COOKIE, cookies);
//            requestHeaders.set("X-CSRF-Token", csrfToken);
//            HttpEntity<String> requestEntity = new HttpEntity<>(requestHeaders);
//
//        }
    }

    private static String extractToken(List<String> cookies, String id) {
        return cookies.stream()
                .filter(cookie -> cookie.contains(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(id + " not found in set_Cookie!"))
                .split(";")[0].split("=")[1];
    }
}
