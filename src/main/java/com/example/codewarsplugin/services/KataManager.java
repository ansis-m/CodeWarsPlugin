package com.example.codewarsplugin.services;


import com.example.codewarsplugin.models.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.net.URI;
import java.net.URLDecoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class KataManager {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final static HttpClient httpClient = HttpClient.newHttpClient();

    private SubmitResponse submitResponse;
    private KataInput input;
    private Token token;

    public KataManager(KataInput input) {
        this.input = input;
    }


    private void getToken(){

        if (token != null) {
            return;
        }

        String csrfToken = LoginService.getCsrfToken();
        String sessionId = LoginService.getSessionId();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://www.codewars.com/api/v1/runner/authorize"))
                .header("X-Csrf-Token", URLDecoder.decode(csrfToken, StandardCharsets.UTF_8))
                .header("Cookie", "CSRF-TOKEN=" + csrfToken + "; _session_id=" + sessionId)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();

            token = objectMapper.readValue(response.body(), Token.class);
            System.out.println("Submit service: Response Body: " + token);

        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }

    public String run(){

        getToken();

        KataOutput output = mapOutput(input);


        System.out.println("Output: \n" + new GsonBuilder().create().toJson(output));

        assert token != null;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://runner.codewars.com/run"))
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token.getToken())
                .POST(HttpRequest.BodyPublishers.ofString(new GsonBuilder().create().toJson(output)))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();

            System.out.println("submit response as string: " + response.body());
            submitResponse = new Gson().fromJson(response.body(), SubmitResponse.class);


            System.out.println("submit response: " + submitResponse.toString());


            notifyServer(output);
            return responseBody;

        } catch (Exception e) {
            System.out.println("An error occurred in run: " + e.getMessage() );
            Arrays.stream(e.getStackTrace()).forEach(System.out::println);
            return null;
        }
    }

    private void notifyServer(KataOutput output) {


        if (submitResponse == null) {
            return;
        }


        getToken();
        String csrfToken = LoginService.getCsrfToken();
        String sessionId = LoginService.getSessionId();


        Map<String, String> code = new HashMap<>();
        code.put("code", output.getCode());
        code.put("fixture", output.getFixture());
        code.put("languageVersion", output.getLanguageVersion());
        code.put("testFramework", output.getTestFramework());
        code.put("token", submitResponse.getToken());

        System.out.println("Notify body: " + new GsonBuilder().create().toJson(code));

        assert token != null;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://www.codewars.com/api/v1/code-challenges" + input.getPath() + "solutions/" + input.getSolutionId() + "/notify"))
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36")
                .header("Content-Type", "application/json")
                .header("Authorization", token.getToken())
                .header("X-Csrf-Token", URLDecoder.decode(csrfToken, StandardCharsets.UTF_8))
                .header("Cookie", "CSRF-TOKEN=" + csrfToken + "; _session_id=" + sessionId)
                .POST(HttpRequest.BodyPublishers.ofString(new GsonBuilder().create().toJson(code)))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("Notify status code: " + response.statusCode() + ": " + response.body());


        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
        }
    }


    public void writeSolution(String solution) {
        this.input.setSetup(solution);
    }

    private KataOutput mapOutput(KataInput input) {

        KataOutput output = new KataOutput();
        output.setLanguage(input.getLanguageName());
        //output.setCode(input.getSetup());

        output.setCode("public class Ship {\n" +
                "  private final double d;\n" +
                "  private final int c;\n" +
                "    \n" +
                "  public Ship(double d, int c) {\n" +
                "    this.d = d;\n" +
                "    this.c = c;\n" +
                "  }\n" +
                "\n" +
                "  public boolean isWorthIt(){\n" +
                "    return d - c * 1.5 >= 20;\n" +
                "  }\n" +
                "} ");
        output.setFixture(input.getFixture());
        output.setTestFramework(input.getTestFramework());
        output.setLanguageVersion("17");
        //output.setLanguageVersion(input.getLanguageVersions().stream().filter(LanguageVersion::isSupported).findFirst().get().getLabel());
        output.setRelayId(input.getSolutionId());
        return output;
    }

    public String commit() {
        getToken();
        String csrfToken = LoginService.getCsrfToken();
        String sessionId = LoginService.getSessionId();

        System.out.println("Path: " + "https://www.codewars.com/api/v1/code-challenges" + input.getPath() + "solutions/" + input.getSolutionId() + "/finalize");

        assert token != null;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://www.codewars.com/api/v1/code-challenges" + input.getPath() + "solutions/" + input.getSolutionId() + "/finalize"))
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36")
                .header("Content-Type", "application/json")
                .header("Authorization", token.getToken())
                .header("X-Csrf-Token", URLDecoder.decode(csrfToken, StandardCharsets.UTF_8))
                .header("Cookie", "CSRF-TOKEN=" + csrfToken + "; _session_id=" + sessionId)
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("Commit result status code: " + response.statusCode());
            return response.body();

        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
            return null;
        }
    }
}
