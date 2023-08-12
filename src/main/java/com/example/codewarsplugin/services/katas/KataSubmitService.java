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
import org.jsoup.internal.StringUtil;

import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
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

        if (token != null && token.getTime().plusSeconds(60).isAfter(LocalDateTime.now())) {
            return;
        }

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
            System.out.println("token: " + response.body());
            token = objectMapper.readValue(response.body(), Token.class);

        } catch (Exception ignored) {}
    }

    public void attempt(){
        KataOutput output = mapOutput();
        try {
            HttpResponse<String> response = call(output);

            if(response.statusCode() >= 200 && response.statusCode() < 300) {
                try{
                    mapSubmitResponse(response.body());
                } catch (Exception e) {
                    System.out.println("Error writing .md: " + e.getMessage());
                    e.printStackTrace();
                }

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
                try{
                    mapSubmitResponse(response.body());
                } catch (Exception e) {
                    System.out.println("Error writing .md: " + e.getMessage());
                    e.printStackTrace();
                }
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
        System.out.println("body: " + body);
        submitResponse = gson.fromJson(body, SubmitResponse.class);
        java.lang.reflect.Type type = new com.google.gson.reflect.TypeToken<Map<String, Object>>(){}.getType();
        Map<String, Object> dataMap = gson.fromJson(body, type);
        var result = dataMap.get("result");
        if (result instanceof Map<?, ?>) {
            createTestResultFile((Map<String, Object>) result, submitResponse);
        }
    }

    public void createTestResultFile(Map<String, Object> source, SubmitResponse submitResponse){
        WriteCommandAction.runWriteCommandAction(project, () -> {
            if (directory.getDirectory() != null) {
                try {
                    VirtualFile testFile = Arrays.stream(directory.getDirectory().getChildren()).filter(child -> !child.isDirectory() && child.getName().equals(FILENAME)).findFirst().orElse(null);
                    if (testFile == null) {
                        testFile = directory.getDirectory().createChildData(this, FILENAME);
                        testFile.refresh(false, true);
                    }
                    String resultString = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(source);


                    System.out.println(resultString);

                    var result = objectMapper.readValue(resultString, Result.class);

                    testFile.setBinaryContent(writeResultToMd(result, submitResponse));
                    OpenFileDescriptor descriptor = new OpenFileDescriptor(project, testFile);
                    FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);
                    fileEditorManager.openTextEditor(descriptor, true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private static byte[] writeResultToMd(Result result, SubmitResponse submitResponse) {

        StringBuilder builder = new StringBuilder();
        writeError(builder, submitResponse);
        if (result.output.size() > 0) {
            writeResult(builder, result);
        }

        builder
                .append("\n\n<style>\n")
                .append("    body {\n")
                .append("    font-family: 'Arial', sans-serif;\n")
                .append("    //background-color: #f5f5f5;\n")
                .append("    padding: 20px;\n")
                .append("}\n")
                .append("        h1 {\n")
                .append("            font-size: 2.5em;\n")
                .append("            border-bottom: 2px solid #3498db;\n")
                .append("            padding-bottom: 10px;\n")
                .append("            margin-bottom: 20px;\n")
                .append("            margin-left: 0em;")
                .append("        }\n")
                .append("        h2 {\n")
                .append("            font-size: 2em;\n")
                .append("            border-bottom: 2px solid #e74c3c;\n")
                .append("            padding-bottom: 8px;\n")
                .append("            margin-bottom: 18px;\n")
                .append("            margin-left: 0em;")
                .append("        }\n")
                .append("        h3 {\n")
                .append("            font-size: 1.5em;\n")
                .append("            margin-bottom: 16px;\n")
                .append("            margin-left: 1em;")
                .append("        }\n")
                .append("        h4 {\n")
                .append("            font-size: 1.2em;\n")
                .append("            margin-bottom: 14px;\n")
                .append("            margin-left: 2em;")
                .append("        }\n")
                .append("        p {\n")
                .append("            font-size: 1.1em;\n")
                .append("            margin-bottom: 12px;\n")
                .append("            line-height: 1.5;\n")
                .append("            margin-left: 3em;")
                .append("        }\n")
                .append("        a {\n")
                .append("            color: #e74c3c;\n")
                .append("            text-decoration: underline;\n")
                .append("            margin-left: 3em;")
                .append("        }")
                .append("        .simple {\n")
                .append("            font-size: 1.1em;\n")
                .append("            margin-bottom: 12px;\n")
                .append("            line-height: 1.5;\n")
                .append("            margin-left: 0em;")
                .append("        }\n")
                .append("</style>");

        return builder.toString().getBytes();
    }

    private static void writeError(StringBuilder builder, SubmitResponse submitResponse) {
        if (!StringUtil.isBlank(submitResponse.getStderr())) {
            builder.append("# ").append(submitResponse.getStderr()).append("\n");
        }
        if (!StringUtil.isBlank(submitResponse.getStdout())) {
            builder.append("Stdout: ").append(submitResponse.getStdout()).append("\n");
        }
        if (!StringUtil.isBlank(submitResponse.getMessage())) {
            builder.append("Message: ").append(submitResponse.getMessage()).append("\n");
        }

    }

    private static void writeResult(StringBuilder builder, Result result) {
        builder.append("# Result\n");
        builder.append("<div class=\"simple\">\n");
        builder.append("passed: ").append(result.passed).append("&nbsp;&nbsp;");
        builder.append("failed: ").append(result.failed);
        builder.append("</div>\n");
        builder.append("\n");
        if (result.timedOut) {
            builder.append("\n## Timed Out\n");
            builder.append("Time Limit: ").append(result.wallTime).append("ms\n");
            builder.append("Test Time: ").append(result.testTime).append("ms\n");
        }
        if (result.serverError) {
            builder.append("\n## Server Error\n");
            builder.append(result.errors).append("errors\n");
            builder.append("error: ").append(result.error).append("\n");
        }
        builder.append("\n## Output");
        if (result.output != null) {
            for(var output : result.output) {
                builder.append("\n### ").append(output.v).append("\n");
                if (output.items != null) {
                    for(var item : output.items) {
                        if (!StringUtil.isBlank(item.v) && item.v.length() > 2) {
                            builder.append("#### ").append(item.v).append("\n");
                        }
                        if(item.items != null) {
                            for(var nestedItem : item.items) {
                                if (nestedItem.t.equals("log")) {
                                    builder.append("**").append(nestedItem.t).append("** : ").append(nestedItem.v.replaceAll("(\r\n|\n|\r)", "  $1")).append("  \n");
                                } else {
                                    builder.append("**").append(nestedItem.t).append("** : ").append(nestedItem.v).append("  \n");
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
