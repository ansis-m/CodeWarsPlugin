package com.example.codewarsplugin.services;

import com.example.codewarsplugin.models.KataInput;
import com.example.codewarsplugin.models.KataRecord;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.URLDecoder;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;



public class KataService {

    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static KataInput getKata(String title) {


        KataRecord record = KataIdService.getKataRecord(title);
        String csrfToken = LoginService.getCsrfToken();
        String sessionId = LoginService.getSessionId();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://www.codewars.com" + record.getPath() + "java/session"))
                .header("X-Csrf-Token", URLDecoder.decode(csrfToken, StandardCharsets.UTF_8))
                .header("Cookie", "CSRF-TOKEN=" + csrfToken + "; _session_id=" + sessionId)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            KataInput kata = objectMapper.readValue(response.body(), KataInput.class);
            kata.setPath(record.getPath());
            return kata;
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
            return null;
        }
    }
}
