package com.example.codewarsplugin.services.katas;

import com.example.codewarsplugin.models.kata.KataRecord;
import com.example.codewarsplugin.services.LoginService;
import com.example.codewarsplugin.state.Vars;
import com.fasterxml.jackson.databind.ObjectMapper;


import javax.swing.*;
import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Pattern;

public class KataIdService {

    private final String BASE_URL = "https://www.codewars.com/api/v1/code-challenges/";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private static final Pattern NON_LATIN_PATTERN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE_PATTERN = Pattern.compile("\\s+");
    public static boolean success = false;

    public KataIdService(){
    }

    public void getKataRecord(String name, Vars vars) {
        success = false;
        KataRecord record = null;
        try{
            SwingWorker<KataRecord, Void> worker = new SwingWorker<KataRecord, Void>() {
                @Override
                protected KataRecord doInBackground() throws InterruptedException, IOException {
                    var slug = generateSlug(name);
                    String url = BASE_URL + slug;
                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create(url))
                            .build();
                    HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                    if (response.statusCode() >= 200 && response.statusCode() < 300) {
                        KataRecord record = objectMapper.readValue(response.body(), KataRecord.class);
                        record.setPath(getKataPath(record.getId()));
                        success = true;
                        return record;
                    }
                    return null;
                }
                @Override
                protected void done() {
                    try{
                        KataRecord record = get();
                        vars.getKataPrompt().complete(record);
                    } catch (Exception e) {
                        vars.getKataPrompt().complete(null);
                    }

                }
            };
            worker.execute();
        } catch (Exception ignored){}
    }

    public String getKataPath(String id) {
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
                startIndex = substring.indexOf("/projects/");
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

    public static String generateSlug(String input) {
        String noWhiteSpace = WHITESPACE_PATTERN.matcher(input.trim()).replaceAll("-");
        String normalized = Normalizer.normalize(noWhiteSpace, Normalizer.Form.NFD);
        return NON_LATIN_PATTERN.matcher(normalized)
                .replaceAll("")
                .toLowerCase(Locale.ENGLISH);
    }
}
