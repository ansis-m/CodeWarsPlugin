package com.example.codewarsplugin.services;


import com.example.codewarsplugin.models.Token;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.URLDecoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class SubmitService {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private final static HttpClient httpClient = HttpClient.newHttpClient();


    public static Token getToken(){
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

            Token token = objectMapper.readValue(response.body(), Token.class);
            System.out.println("Submit service: Response Body: " + token);
            return token;

        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
            return null;
        }
    }

    public static String run(){

        Token token = getToken();

        String body = "{\"language\":\"kotlin\",\"code\":\"fun include(arr: IntArray, item : Int): Boolean {\\n    return true\\n}\\n\",\"fixture\":\"hq8gZgB4fyHRGft7NEInR/zr3oGusN5Qc2FUZ8NJj5hR4MaJ2L8jmmC5aaDn\\nvgxx30eWm6v+cSO2AkeTvx+56NIkXMd5Z7uB78o+n/ooRbswNqWAQOzeM95N\\nsFxzFZXsylK6cF6Ml01QyoSu4z0TeF98plbCp6aM/R8q1/ie6xm6y0UezZxm\\naTmkrN1a+kxVBERlZcCDuhihxIdN+JwJpsgyz9a5/t2WdvldXWPhGd15JNz5\\nc8jBW/jpQUEOuvynXH95xe7hVpcATDO43rAZEtmrwJLVL1ZJ/sAvnvrXHJrI\\nAWmDVBR3mEXXT7EXVSJJkhLKqw6Z4I2/XmhvSuQGgtWyKlhWXPkQSRM3r1tA\\niQuCFc72jnhRm/04WyE443UWkVcQVMEaZjVTn7ghrqJu2NJoqoYIwm0EY7DN\\nlJUTqjeGnA81FBz3IKELOItjoiwcVTUU4ER4TbSwnygit7+wGgCpnmpbbhMS\\nHEcrHQn8hkjt9dM7OjtiSLlOLr0DyYOZkFjVkIwjpvb2Rf6/1TtV5WgtdUFy\\nNmH7LfJsjH7GgIVXR0R6Cu4lm7moPFNiA0iqZyU773Kq1Klt+9rUzMUrRgC3\\n2VmBVP5WkBzYmGqDBsgFZ7498fmkMPKoIN3xYgV6pX89F+3t+cT55y1s+wo7\\nYr3SKlwK/XawHhzfPjhXIFoIglr9DWaH6KVXoQXYdpPqFFLWib13FZdyMnEM\\n5Hyo4oa05vxJAJzChvx98/NFYo07nd0z8uybG/9xooz4NFABMNzYrKR+weDN\\nLj+uU2GjIcCRgKOBafcigtJaXgT3y3hhKt3rX2tC5BcnU5/TYct//h5HdUrs\\nqYj0Jp2zv7eW/azLuu+cdfjj/48prKBvYsDSKF6E4/8Q3lE06900H/HT2ffU\\nGGx2WmaBtynpQCuUSPqcI+iiLeE5msK9NTiuvKiOkdCvayASO4tbrQYCnHJh\\n41oyuXL8YgDTFRW65E8vAATg22heohRPNHnCKfYfvdZ8+tWX/tF/kgDf6M8y\\nsqBAY5OzUSQwyzH44Yaab7hzJt5MNUcYg68Zbhl04p7lfy+re0MByGt5RrVH\\nNw4Bp9/N+OHyE88yIxy8CGNFUtcczQ==\\n--nSjL4hDmaxwcLt6E5OBCHw==--u\",\"setup\":\"\",\"testFramework\":\"junit\",\"languageVersion\":\"1.5\",\"relayId\":\"6494ab7f8673b3491ce5a119\",\"ciphered\":[\"setup\",\"fixture\"],\"channel\":\"runner:1607fef7-63ac-b291-7473-b13cdf955d23\",\"successMode\":null}";


        assert token != null;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://runner.codewars.com/run"))
                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/114.0.0.0 Safari/537.36")
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + token.getToken())
                .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                .build();

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            String responseBody = response.body();


            System.out.println("Submit service: Submitted the solution: " + responseBody);
            return responseBody;

        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
            return null;
        }
    }

}
