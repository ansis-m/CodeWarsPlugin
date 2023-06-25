//package com.example.codewarsplugin.services;
//
//import org.springframework.http.*;
//import org.springframework.web.client.RestTemplate;
//
//import java.net.URI;
//import java.net.URLDecoder;
//import java.nio.charset.StandardCharsets;
//
//
//public class KataService {
//
//    private static RestTemplate restTemplate = new RestTemplate();
//    private static HttpHeaders headers = new HttpHeaders();
//
//
//    public static void getKata() {
//        headers.set("X-Csrf-Token", URLDecoder.decode(LoginService.getCsrfToken(), StandardCharsets.UTF_8));
//        headers.set("Cookie", "CSRF-TOKEN=" + LoginService.getCsrfToken() + "; _session_id=" + LoginService.getSessionId());
//        headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36");
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        RequestEntity<Void> requestEntity = new RequestEntity<>(headers, HttpMethod.POST, URI.create("https://www.codewars.com/kata/projects/6190cdfa63c55a00373dd825/kotlin/session"));
//        ResponseEntity<String> responseEntity = restTemplate.exchange(requestEntity, String.class);
//        String responseBody = responseEntity.getBody();
//        System.out.println("Kata Response Body: " + responseBody);
//    }
//}
