package com.cw.codewarsplugin.services.katas;


import com.cw.codewarsplugin.config.StringConstants;
import com.cw.codewarsplugin.models.Token;
import com.cw.codewarsplugin.models.kata.*;
import com.cw.codewarsplugin.services.cookies.CookieService;
import com.cw.codewarsplugin.state.Store;
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
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class KataSubmitService {

    private static final ObjectMapper objectMapper = new ObjectMapper();
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
                .uri(URI.create(StringConstants.AUTHORIZE_URL))
                .header("X-Csrf-Token", URLDecoder.decode(csrfToken, StandardCharsets.UTF_8))
                .header("Cookie", "CSRF-TOKEN=" + csrfToken + "; _session_id=" + sessionId)
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofByteArray("{}".getBytes(StandardCharsets.UTF_8)))
                .build();

        try {
            HttpResponse<String> response = CookieService.getHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
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

        return CookieService.getHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
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
                .uri(URI.create(StringConstants.RECORD_URL + input.getPath() + "solutions/" + input.getSolutionId() + "/notify"))
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36")
                .header("Content-Type", "application/json")
                .header("Authorization", token.getToken())
                .header("X-Csrf-Token", URLDecoder.decode(csrfToken, StandardCharsets.UTF_8))
                .header("Cookie", "CSRF-TOKEN=" + csrfToken + "; _session_id=" + sessionId)
                .POST(HttpRequest.BodyPublishers.ofString(new GsonBuilder().create().toJson(code)))
                .build();

        CookieService.getHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
    }

    private KataOutput mapOutput() {

        KataOutput output = new KataOutput();
        output.setLanguage(input.getLanguageName());
        output.setCode(readFile(true));
        output.setFixture(input.getFixture());
        output.setTestFramework(input.getTestFramework());
        output.setLanguageVersion(input.getActiveVersion());
        output.setRelayId(input.getSolutionId());
        return output;
    }
    private KataOutput mapTestOutput() {
        return mapOutput().setCiphered(new String[] {"setup"}).setFixture(readFile(false));
    }

    private String readFile(boolean workFile) {

        AtomicReference<String> atomicString = new AtomicReference<>("");
        ApplicationManager.getApplication().runReadAction(() -> {
            FileDocumentManager documentManager = FileDocumentManager.getInstance();
            Document document = documentManager.getDocument(workFile? directory.getWorkFile() : directory.getTestFile());
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
                .uri(URI.create(StringConstants.RECORD_URL + input.getPath() + "solutions/" + input.getSolutionId() + "/finalize"))
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36")
                .header("Content-Type", "application/json")
                .header("Authorization", token.getToken())
                .header("X-Csrf-Token", URLDecoder.decode(csrfToken, StandardCharsets.UTF_8))
                .header("Cookie", "CSRF-TOKEN=" + csrfToken + "; _session_id=" + sessionId)
                .POST(HttpRequest.BodyPublishers.ofByteArray("{}".getBytes(StandardCharsets.UTF_8)))
                .build();
        try {
            var response = CookieService.getHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
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
            createTestResultFile((Map<String, Object>) result, submitResponse);
        }
    }

    public void createTestResultFile(Map<String, Object> source, SubmitResponse submitResponse){
        WriteCommandAction.runWriteCommandAction(project, () -> {
            if (directory.getDirectory() != null) {
                try {
                    VirtualFile testFile = Arrays.stream(directory.getDirectory().getChildren()).filter(child -> !child.isDirectory() && child.getName().equals(StringConstants.FILENAME)).findFirst().orElse(null);
                    if (testFile == null) {
                        testFile = directory.getDirectory().createChildData(this, StringConstants.FILENAME);
                        testFile.refresh(false, true);
                    }
                    String resultString = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(source);
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
        appendError(builder, submitResponse);
        if (result.output != null && result.output.size() > 0) {
            appendResult(builder, result);
        }
        appendStyling(builder);
        return builder.toString().getBytes();
    }

    private static void appendError(StringBuilder builder, SubmitResponse submitResponse) {
        if (!StringUtil.isBlank(submitResponse.getStderr())) {
            builder.append("## Stderr  \n");
            builder.append("\n\n<div class=\"simple\">\n")
                    .append("Stderr: ").append(submitResponse.getStderr()).append("&nbsp;&nbsp;&nbsp;&nbsp;");
            if (!StringUtil.isBlank(submitResponse.getStdout())) {
                builder.append("Stdout: ").append(submitResponse.getStdout()).append("&nbsp;&nbsp;&nbsp;&nbsp;");
            }

            if (!StringUtil.isBlank(submitResponse.getMessage())) {
                builder.append("Message: ").append(submitResponse.getMessage());
            }
            builder.append("</div>\n\n");
        }
    }

    private static void appendResult(StringBuilder builder, Result result) {

        builder.append("## Result\n")
        .append("<div class=\"simple\">\n")
        .append("passed: ").append(result.passed).append("&nbsp;&nbsp;&nbsp;&nbsp;")
        .append("failed: ").append(result.failed).append("&nbsp;&nbsp;&nbsp;&nbsp;")
        .append("errors: ").append(result.errors).append("</div>\n\n");
        if (result.timedOut) {
            builder.append("\n## Timed Out\n").append("<div class=\"simple\">\n")
            .append("Time Limit: ").append(result.wallTime).append("&nbsp;ms\n").append("&nbsp;&nbsp;&nbsp;&nbsp;")
            .append("Test Time: ").append(result.testTime).append("&nbsp;ms\n").append("</div>\n");
        }
        if (result.serverError) {
            builder.append("\n## Server Error\n")
            .append(result.errors).append("errors\n")
            .append("error: ").append(result.error).append("\n");
        }
        builder.append("\n## Output\n");
        appendOutput(builder, result.output, 1);
    }

    private static void appendOutput(StringBuilder builder, List<Result.Output> outputs, int depth) {
        if (outputs == null) return;
        for (var output : outputs) {
            if (!StringUtil.isBlank(output.v) && output.v.length() > 2) {
                String title = output.t.equals("log") ? "log" : output.v;
                generateHeader(builder, depth, title);
                if (output.t.equals("log")) {
                    builder.append(output.v.replaceAll("(\r\n|\n|\r)", "  $1"));
                } else if (depth > 2) {
                    builder.append("+ **").append(output.t).append("** : ").append(output.v).append("  \n");
                }
            }
            appendOutput(builder, output.items, depth + 1);
        }
    }

    private static void generateHeader(StringBuilder builder, int depth, String content) {

        switch (depth) {
            case 0:
                builder.append("\n## ").append(content).append("  \n");
                break;
            case 1:
                builder.append("\n### ").append(content).append("  \n");
                break;
            case 2:
                builder.append("\n#### ").append(content).append("  \n");
                break;
            case 3: if(content.equals("log"))
                        builder.append("+ **").append("log").append("** : ");
        }
    }


    private static void appendStyling(StringBuilder builder) {
        builder.append("\n\n<style>\n")
                .append("body { font-family: 'Arial', sans-serif; padding: 20px; }\n")
                .append("h1 { font-size: 2.5em; border-bottom: 2px solid #3498db; padding-bottom: 10px; margin-bottom: 20px; }\n")
                .append("h2 { font-size: 2em; border-bottom: 2px solid #e74c3c; padding-bottom: 8px; margin-bottom: 18px; }\n")
                .append("h3 { font-size: 1.5em; margin-bottom: 16px; margin-left: 1em; }\n")
                .append("h4 { font-size: 1.2em; margin-bottom: 14px; margin-left: 2em; }\n")
                .append(".simple { font-size: 1.1em; margin-bottom: 12px; line-height: 1.5; margin-left: 0em; }\n")
                .append("p { font-size: 1.1em; margin-bottom: 12px; line-height: 1.5; margin-left: 3em; }\n")
                .append("a { color: #e74c3c; text-decoration: underline; margin-left: 3em; }\n")
                .append("ul { padding-left: 5em; }\n")
                .append("ul li { text-indent: 0em; }\n")
                .append("</style>");
    }
}
