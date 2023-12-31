package com.cw.codewarsplugin.services.katas;

import com.cw.codewarsplugin.config.StringConstants;
import com.cw.codewarsplugin.models.kata.KataInput;
import com.cw.codewarsplugin.models.kata.KataRecord;
import com.cw.codewarsplugin.services.cookies.CookieService;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.URLDecoder;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;


public class KataInputService {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static KataInput getKata(KataRecord record) {

        String csrfToken = CookieService.getCsrfToken();
        String sessionId = CookieService.getSessionId();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(StringConstants.KATA_URL + record.getPath() + record.getSelectedLanguage() + "/session"))
                .header("X-Csrf-Token", URLDecoder.decode(csrfToken, StandardCharsets.UTF_8))
                .header("Cookie", "CSRF-TOKEN=" + csrfToken + "; _session_id=" + sessionId)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofByteArray("{}".getBytes(StandardCharsets.UTF_8)))
                .build();
        try {
            HttpResponse<String> response = CookieService.getHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            KataInput kata = objectMapper.readValue(response.body(), KataInput.class);
            kata.setPath(record.getPath());
            return kata;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }
}
