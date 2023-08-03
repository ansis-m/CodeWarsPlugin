package com.example.codewarsplugin.services.katas;

import com.example.codewarsplugin.models.kata.KataRecord;
import com.example.codewarsplugin.services.login.LoginService;
import com.fasterxml.jackson.databind.ObjectMapper;


import javax.swing.*;
import java.net.URI;
import java.net.URLDecoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.Locale;
import java.util.regex.Pattern;

import static com.example.codewarsplugin.config.StringConstants.KATA_URL;
import static com.example.codewarsplugin.config.StringConstants.RECORD_URL;

public class KataRecordService {
    private static final Pattern NON_LATIN_PATTERN = Pattern.compile("[^\\w-]");
    private static final Pattern WHITESPACE_PATTERN = Pattern.compile("\\s+");
    public static boolean success = false;
    private static KataRecord record;

    public static void getKataRecord(String name, KataRecordServiceClient client) {
        success = false;
        final ObjectMapper objectMapper = new ObjectMapper();
        final HttpClient httpClient = HttpClient.newHttpClient();
        final KataRecord[] records = new KataRecord[1];

        try{
            SwingWorker<KataRecord, Void> worker = new SwingWorker<KataRecord, Void>() {
                @Override
                protected KataRecord doInBackground() {
                    var slug = generateSlug(name);
                    String url = RECORD_URL + "/" + slug;
                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create(url))
                            .build();
                    try  {
                        HttpResponse<String> response = LoginService.getHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
                        KataRecord record = objectMapper.readValue(response.body(), KataRecord.class);
                        record.setPath(getKataPath(record.getId()));
                        success = true;
                        return record;
                    } catch (Exception e) {
                        return null;
                    }
                }
                @Override
                protected void done() {
                    try{
                        var result = get();
                        if (result != null) {
                            client.processKataRecord(result);
                            records[0] = result;
                        } else {
                            client.processKataRecordNotFound(null);
                        }
                    } catch (Exception e) {
                        client.processKataRecordNotFound(null);
                    }
                }
            };
            worker.execute();
            record = records[0];
        } catch (Exception ignored){}
    }

    public static String getKataPath(String id) {
        String csrfToken = LoginService.getCsrfToken();
        String sessionId = LoginService.getSessionId();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format(KATA_URL + "/%s/train/java", id)))
                .header("X-Csrf-Token", URLDecoder.decode(csrfToken, StandardCharsets.UTF_8))
                .header("Cookie", "CSRF-TOKEN=" + csrfToken + "; _session_id=" + sessionId)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36")
                .header("Content-Type", "application/json")
                .GET()
                .build();

        try {
            HttpResponse<String> response = LoginService.getHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();

            String script = findScriptWithAppSetup(responseBody);
            int startIndex = script.indexOf("session");
            int endIndex = script.lastIndexOf("session");

            if (startIndex != -1 && endIndex != -1 && startIndex != endIndex) {

                String substring = script.substring(startIndex, endIndex + "session".length());
                startIndex = substring.indexOf("/projects/");
                endIndex = substring.lastIndexOf("%7Blanguage%7D");
                return substring.substring(startIndex, endIndex);
            } else {
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static KataRecord getRecord() {
        return record;
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
