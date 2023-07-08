package com.example.codewarsplugin.services;

import com.example.codewarsplugin.models.KataRecord;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.net.URI;
import java.net.URLDecoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
                kata.setPath(getKataPath(kata.getId()));
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

    public static String getKataPath(String id) {
        String csrfToken = LoginService.getCsrfToken();
        String sessionId = LoginService.getSessionId();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("https://www.codewars.com/kata/%s/train/java", id)))
                .header("X-Csrf-Token", URLDecoder.decode(csrfToken, StandardCharsets.UTF_8))
                .header("Cookie", "CSRF-TOKEN=" + csrfToken + "; _session_id=" + sessionId)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36")
                .header("Content-Type", "application/json")
                .GET()
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();

            String script = findScriptWithAppSetup(responseBody);
            int startIndex = script.indexOf("session");
            int endIndex = script.lastIndexOf("session");

            if (startIndex != -1 && endIndex != -1 && startIndex != endIndex) {

                String substring = script.substring(startIndex, endIndex + "session".length());
                startIndex = substring.indexOf("/kata/projects/");
                endIndex = substring.lastIndexOf("%7Blanguage%7D");

                System.out.println("Substring: " + substring);
                return substring.substring(startIndex, endIndex);
            } else {
                System.out.println("Substring not found.");
                return "";
            }
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
            Arrays.stream(e.getStackTrace()).forEach(System.out::println);
            return null;
        }
    }

    private static String findScriptWithAppSetup(String html) {
        int startIndex = html.indexOf("App.setup(");
        int endIndex = html.indexOf("</script>", startIndex);
        return html.substring(startIndex, endIndex);
    }

    private static String formatString(String id) {
        return id.toLowerCase().replaceAll("\\s+-\\s+", "-").replaceAll("-\\s+|\\s+-|\\s+", "-");
    }
}
