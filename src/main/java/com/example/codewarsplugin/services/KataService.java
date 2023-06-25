package com.example.codewarsplugin.services;

import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;


public class KataService {

    private static RestTemplate restTemplate = new RestTemplate();
    private static HttpHeaders headers = new HttpHeaders();


    public static void getKata() {
        headers.set("X-Csrf-Token", URLDecoder.decode("mwajn95JK6NKsk9g5Su0a4VgWnfAJGDZ726VZ3M%2FzWJe6Qu7weAKrwAAnkvnWB%2F9clT3jFIGStHzE6QQv%2Fhl%2Bw%3D%3D", StandardCharsets.UTF_8));
        headers.set("Cookie", "CSRF-TOKEN=" + URLDecoder.decode("mwajn95JK6NKsk9g5Su0a4VgWnfAJGDZ726VZ3M%2FzWJe6Qu7weAKrwAAnkvnWB%2F9clT3jFIGStHzE6QQv%2Fhl%2Bw%3D%3D", StandardCharsets.UTF_8)+ "; _session_id=d22f0e4a44c7f4ff3e6f2c2f5c6e1220");
        headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36");
        headers.setContentType(MediaType.APPLICATION_JSON);
        RequestEntity<Void> requestEntity = new RequestEntity<>(headers, HttpMethod.POST, URI.create("https://www.codewars.com/kata/projects/6190cdfa63c55a00373dd825/kotlin/session"));
        ResponseEntity<String> responseEntity = restTemplate.exchange(requestEntity, String.class);
        String responseBody = responseEntity.getBody();
        System.out.println("Kata Response Body: " + responseBody);
    }
}
