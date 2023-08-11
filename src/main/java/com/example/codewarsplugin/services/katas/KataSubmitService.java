package com.example.codewarsplugin.services.katas;


import com.example.codewarsplugin.models.*;
import com.example.codewarsplugin.models.kata.*;
import com.example.codewarsplugin.services.cookies.CookieService;
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
import com.intellij.openapi.project.Project;
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
import java.util.concurrent.atomic.AtomicReference;

import static com.example.codewarsplugin.config.StringConstants.*;

public class KataSubmitService {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final static HttpClient httpClient = HttpClient.newHttpClient();
    private SubmitResponse submitResponse;
    private final KataInput input;
    private final KataDirectory directory;
    private final KataSubmitServiceClient client;
    private Token token;
    private final Gson gson = new Gson();
    private final Project project;

    public KataSubmitService(Store store, KataDirectory directory, KataSubmitServiceClient client) {
        this.input = directory.getInput();
        this.directory = directory;
        this.client = client;
        this.project = store.getProject();
    }


    private void getToken(){

        String csrfToken = CookieService.getCsrfToken();
        String sessionId = CookieService.getSessionId();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(AUTHORIZE_URL))
                .header("X-Csrf-Token", URLDecoder.decode(csrfToken, StandardCharsets.UTF_8))
                .header("Cookie", "CSRF-TOKEN=" + csrfToken + "; _session_id=" + sessionId)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            token = objectMapper.readValue(response.body(), Token.class);

        } catch (Exception ignored) {}
    }

    public void attempt(){
        KataOutput output = mapOutput();
        try {
            HttpResponse<String> response = call(output);

            if(response.statusCode() >= 200 && response.statusCode() < 300) {
                mapSubmitResponse(response.body());
                notifyServer(output);
                client.notifyAttemptSuccess(submitResponse);
            } else {
                client.notifyBadAttemptStatusCode(response);
            }
        } catch (Exception e) {
            client.notifyAttemptRunException(e);
        }
    }

    public void test() {
        KataOutput output = mapTestOutput();
        try {
            HttpResponse<String> response = call(output);
            if(response.statusCode() >= 200 && response.statusCode() < 300) {
                mapSubmitResponse(response.body());
                notifyServer(output);
                client.notifyTestSuccess(submitResponse);
            } else {
                client.notifyBadTestStatusCode(response);
            }
        } catch (Exception e) {
            client.notifyTestRunException(e);
        }
    }

    private HttpResponse<String> call(KataOutput output) throws IOException, InterruptedException {
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

    private void notifyServer(KataOutput output) throws IOException, InterruptedException {

        if (submitResponse == null) {
            return;
        }

        getToken();
        String csrfToken = CookieService.getCsrfToken();
        String sessionId = CookieService.getSessionId();

        Map<String, String> code = new HashMap<>();
        code.put("code", output.getCode());
        code.put("fixture", output.getFixture());
        code.put("languageVersion", output.getLanguageVersion());
        code.put("testFramework", output.getTestFramework());
        code.put("token", submitResponse.getToken());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(RECORD_URL + input.getPath() + "solutions/" + input.getSolutionId() + "/notify"))
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36")
                .header("Content-Type", "application/json")
                .header("Authorization", token.getToken())
                .header("X-Csrf-Token", URLDecoder.decode(csrfToken, StandardCharsets.UTF_8))
                .header("Cookie", "CSRF-TOKEN=" + csrfToken + "; _session_id=" + sessionId)
                .POST(HttpRequest.BodyPublishers.ofString(new GsonBuilder().create().toJson(code)))
                .build();

        httpClient.send(request, HttpResponse.BodyHandlers.ofString());
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

        AtomicReference<String> atomicString = new AtomicReference<>("");
        ApplicationManager.getApplication().runReadAction(() -> {
            FileDocumentManager documentManager = FileDocumentManager.getInstance();
            Document document = documentManager.getDocument(directory.getWorkFile());
            if (document != null) {
                atomicString.set(stripPackage(document.getText()));
            }
        });
        return atomicString.get();
    }

    private String stripPackage(String text) {
        int begin = text.indexOf("package");
        int semicolon = text.indexOf(';');

        if(begin != -1 && semicolon != -1 && semicolon > begin && text.length() > semicolon + 1) {
            return text.substring(semicolon + 1);
        }
        return text;
    }

    public void commit() {
        getToken();
        String csrfToken = CookieService.getCsrfToken();
        String sessionId = CookieService.getSessionId();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(RECORD_URL + input.getPath() + "solutions/" + input.getSolutionId() + "/finalize"))
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36")
                .header("Content-Type", "application/json")
                .header("Authorization", token.getToken())
                .header("X-Csrf-Token", URLDecoder.decode(csrfToken, StandardCharsets.UTF_8))
                .header("Cookie", "CSRF-TOKEN=" + csrfToken + "; _session_id=" + sessionId)
                .POST(HttpRequest.BodyPublishers.noBody())
                .build();
        try {
            var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                client.notifyCommitSuccess(response);
            } else {
                client.notifyCommitFailed(response);
            }

        } catch (Exception e) {
            client.notifyCommitException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private void mapSubmitResponse(String body) {
        submitResponse = gson.fromJson(body, SubmitResponse.class);
        java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<Map<String, Object>>(){}.getType();
        Map<String, Object> dataMap = gson.fromJson(body, type);
        var result = dataMap.get("result");
        if (result instanceof Map<?, ?>) {
            createTestResultFile((Map<String, Object>) result);
        }
    }

    public void createTestResultFile(Map<String, Object> source){
        WriteCommandAction.runWriteCommandAction(project, () -> {
            if (directory.getDirectory() != null) {
                try {
                    VirtualFile testFile = Arrays.stream(directory.getDirectory().getChildren()).filter(child -> !child.isDirectory() && child.getName().equals(FILENAME)).findFirst().orElse(null);
                    if (testFile == null) {
                        testFile = directory.getDirectory().createChildData(this, FILENAME);
                        testFile.refresh(false, true);
                    }
                    testFile.setBinaryContent(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(source).getBytes());
                    OpenFileDescriptor descriptor = new OpenFileDescriptor(project, testFile);
                    FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
                    fileEditorManager.openTextEditor(descriptor, true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
