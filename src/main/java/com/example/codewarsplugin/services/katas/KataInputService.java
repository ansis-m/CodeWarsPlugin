package com.example.codewarsplugin.services.katas;

import com.example.codewarsplugin.models.kata.KataInput;
import com.example.codewarsplugin.models.kata.KataRecord;
import com.example.codewarsplugin.services.LoginService;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.*;
import java.net.URI;
import java.net.URLDecoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;


public class KataInputService {

    private static final HttpClient httpClient = HttpClient.newHttpClient();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static void getKata(KataRecord record, KataInputServiceClient client) {

        String csrfToken = LoginService.getCsrfToken();
        String sessionId = LoginService.getSessionId();

        SwingWorker<KataInput, Void> worker = new SwingWorker<KataInput, Void>() {
            @Override
            protected KataInput doInBackground() {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create("https://www.codewars.com/kata" + record.getPath() + record.getSelectedLanguage() + "/session"))
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

            @Override
            protected void done(){
                try {
                    if(get() == null) {
                        client.processInputNotFound(new RuntimeException("KataInputService HttpRequest failed!"));
                    } else {
                        client.processKataInput(get());
                    }
                } catch (Exception e) {
                    client.processInputNotFound(e);
                }
            }
        };
        worker.execute();
    }
}
