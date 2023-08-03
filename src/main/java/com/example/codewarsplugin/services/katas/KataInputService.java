package com.example.codewarsplugin.services.katas;

import com.example.codewarsplugin.models.kata.KataInput;
import com.example.codewarsplugin.models.kata.KataRecord;
import com.example.codewarsplugin.services.login.LoginService;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.*;
import java.net.URI;
import java.net.URLDecoder;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import static com.example.codewarsplugin.config.StringConstants.KATA_URL;


public class KataInputService {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static KataInput getKata(KataRecord record) {

        String csrfToken = LoginService.getCsrfToken();
        String sessionId = LoginService.getSessionId();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(KATA_URL + record.getPath() + record.getSelectedLanguage() + "/session"))
                .header("X-Csrf-Token", URLDecoder.decode(csrfToken, StandardCharsets.UTF_8))
                .header("Cookie", "CSRF-TOKEN=" + csrfToken + "; _session_id=" + sessionId)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        try {
            HttpResponse<String> response = LoginService.getHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            KataInput kata = objectMapper.readValue(response.body(), KataInput.class);
            kata.setPath(record.getPath());
            return kata;

        } catch (Exception e) {
            return null;
        }

    }
}
