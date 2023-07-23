package com.example.codewarsplugin.services.katas;


import com.example.codewarsplugin.models.*;
import com.example.codewarsplugin.models.kata.*;
import com.example.codewarsplugin.services.login.LoginService;
import com.example.codewarsplugin.services.project.MyProjectManager;
import com.example.codewarsplugin.state.Store;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.OpenFileDescriptor;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class KataSubmitService {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final static HttpClient httpClient = HttpClient.newHttpClient();
    private SubmitResponse submitResponse;
    private KataInput input;
    private KataDirectory directory;
    private KataSubmitServiceClient client;
    private Token token;
    private Gson gson = new Gson();
    final String FILENAME = "test.json";

    public KataSubmitService(Store store, KataDirectory directory, KataSubmitServiceClient client) {
        this.input = directory.getInput();
        this.directory = directory;
        this.client = client;
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

    public void attempt(){

        KataOutput output = mapOutput();
        try {
            HttpResponse<String> response = run(output);

            if(response.statusCode() >= 200 && response.statusCode() < 300) {
                mapSubmitResponse(response.body());
                notifyServer(output);
                client.notifyAttemptSuccess(submitResponse);
            } else {
                client.notifyBadStatusCode(response);
            }
        } catch (Exception e) {
            client.notifyRunFailed(e);
        }
    }

    public void test() {
        KataOutput output = mapTestOutput();

        try {
            HttpResponse<String> response = run(output);
            if(response.statusCode() >= 200 && response.statusCode() < 300) {
                mapSubmitResponse(response.body());
                notifyServer(output);
                client.notifyTestSuccess(submitResponse);
            } else {
                client.notifyBadStatusCode(response);
            }
        } catch (Exception e) {
            client.notifyRunFailed(e);
        }
    }

    private HttpResponse<String> run(KataOutput output) throws IOException, InterruptedException {
        getToken();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://runner.codewars.com/run"))
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token.getToken())
                .POST(HttpRequest.BodyPublishers.ofString(new GsonBuilder().create().toJson(output)))
                .build();

        return httpClient.send(request, HttpResponse.BodyHandlers.ofString());
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


    private void writeSolution(String solution) {
        this.input.setSetup(solution);
    }

    private KataOutput mapOutput() {

        KataOutput output = new KataOutput();
        output.setLanguage(input.getLanguageName());
        String code = readWorkFile();
        output.setCode(code);
        output.setFixture(input.getFixture());
        output.setTestFramework(input.getTestFramework());
        output.setLanguageVersion(input.getActiveVersion());
        output.setRelayId(input.getSolutionId());
        return output;
    }
    private KataOutput mapTestOutput() {
        return mapOutput().setCiphered(new String[] {"setup"}).setFixture(input.getExampleFixture());
    }

    private String readWorkFile() {
        ApplicationManager.getApplication().invokeAndWait(() -> {
            FileDocumentManager.getInstance().getDocument(directory.getWorkFile());
        });

        FileDocumentManager documentManager = FileDocumentManager.getInstance();
        Document document = documentManager.getDocument(directory.getWorkFile());

        if (document != null) {
            return stripPackage(document.getText());
        }

        return "";
    }

    private String stripPackage(String text) {
        int begin = text.indexOf("package");
        int semicolon = text.indexOf(';');

        if(begin != -1 && semicolon != -1 && semicolon > begin && text.length() > semicolon + 1) {
            return text.substring(semicolon + 1);
        }
        return text;
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

    private void mapSubmitResponse(String body) {
        submitResponse = gson.fromJson(body, SubmitResponse.class);

        Map<String, Object> dataMap = gson.fromJson(body, Map.class);
        Map<String, Object> result = (Map<String, Object>) dataMap.get("result");
        createTestResultFile(result);
    }


    public void createTestResultFile(Map<String, Object> source){
        WriteCommandAction.runWriteCommandAction(MyProjectManager.getProject(), () -> {
            if (directory.getDirectory() != null) {
                try {
                    VirtualFile testFile = Arrays.stream(directory.getDirectory().getChildren()).filter(child -> !child.isDirectory() && child.getName().equals(FILENAME)).findFirst().orElse(null);
                    if (testFile == null) {
                        testFile = directory.getDirectory().createChildData(this, FILENAME);
                        testFile.refresh(false, true);
                    }
                    testFile.setBinaryContent(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(source).getBytes());
                    OpenFileDescriptor descriptor = new OpenFileDescriptor(MyProjectManager.getProject(), testFile);
                    FileEditorManager fileEditorManager = FileEditorManager.getInstance(MyProjectManager.getProject());
                    fileEditorManager.openTextEditor(descriptor, true);
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }
}
