package com.example.codewarsplugin.services;

import com.example.codewarsplugin.models.KataInput;
import com.example.codewarsplugin.models.KataRecord;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.net.URI;
import java.net.URLDecoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class KataIdService {

    private static final String BASE_URL = "https://www.codewars.com/api/v1/code-challenges/";
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final HttpClient httpClient = HttpClient.newHttpClient();

    private KataIdService(){
    }

    public static KataRecord getKataRecord(String name) {
        name = formatString(name);



        String url = BASE_URL + name;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                KataRecord kata = objectMapper.readValue(response.body(), KataRecord.class);
                return kata;
            } else {
                System.out.println("Request failed with status: " + response.statusCode());
                return null;
            }
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
            return null;
        }
    }

    public static String getKataId(String name) {
        KataRecord kata = getKataRecord(name);
        System.out.println("Kata record: " + kata);


        String csrfToken = LoginService.getCsrfToken();
        String sessionId = LoginService.getSessionId();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("https://www.codewars.com/kata/%s/train/java", kata.getId())))
                .header("X-Csrf-Token", URLDecoder.decode(csrfToken, StandardCharsets.UTF_8))
                .header("Cookie", "CSRF-TOKEN=" + csrfToken + "; _session_id=" + sessionId)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36")
                .header("Content-Type", "application/json")
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();
            return responseBody;


        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
            return null;
        }

    }


    private static String formatString(String id) {
        return id.toLowerCase().replaceAll("\\s+-\\s+", "-").replaceAll("-\\s+|\\s+-|\\s+", "-");
    }
}
