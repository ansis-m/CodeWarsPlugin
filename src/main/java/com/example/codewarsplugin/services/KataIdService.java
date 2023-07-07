package com.example.codewarsplugin.services;

import com.example.codewarsplugin.models.KataRecord;
import com.fasterxml.jackson.databind.ObjectMapper;


import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KataIdService {

    private static final String BASE_URL = "https://www.codewars.com/api/v1/code-challenges/";
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final HttpClient httpClient = HttpClient.newHttpClient();

    private KataIdService(){
    }

    public static KataRecord getKataRecord(String id) {
        id = formatString(id);



        String url = BASE_URL + id;
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

    private static String formatString(String id) {
        return id.toLowerCase().replaceAll("\\s+-\\s+", "-").replaceAll("-\\s+|\\s+-|\\s+", "-");
    }
}
