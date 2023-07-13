package com.example.codewarsplugin.services;

import com.example.codewarsplugin.models.user.User;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.Map;

public class UserService {


    private static ChromeDriver chromeDriver;

    public static User getUser() {
        chromeDriver = WebDriver.getChromeDriver();
//        String responseBody = (String) chromeDriver.executeScript(
//                "var xhr = new XMLHttpRequest();" +
//                        "xhr.open('GET', 'https://www.codewars.com/dashboard', false);" +
//                        "xhr.send();" +
//                        "return xhr.responseText;"
//        );
        //System.out.println("Response Body: " + responseBody);

        // Execute JavaScript code and extract the desired data
        //chromeDriver.get("https://www.codewars.com/dashboard");

        JavascriptExecutor jsExecutor = (JavascriptExecutor) chromeDriver;
        String serializedObjects = (String) jsExecutor.executeScript(
                "function serialize(obj) {" +
                        "  const seen = new WeakSet();" +
                        "  return JSON.stringify(obj, (key, value) => {" +
                        "    if (typeof value === 'object' && value !== null) {" +
                        "      if (seen.has(value)) {" +
                        "        return '[Circular Reference]';" +
                        "      }" +
                        "      seen.add(value);" +
                        "    }" +
                        "    return value;" +
                        "  });" +
                        "}" +
                        "return serialize(window);"
        );


        // Process the 'user' object as needed
        // ...

        // Print the extracted data
        System.out.println(serializedObjects);

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode rootNode = objectMapper.readTree(serializedObjects);

            // Find the object that contains the unique field "career_paths"
            JsonNode targetObject = findObjectWithField(rootNode, "career_paths");

            // Use the targetObject as needed
            System.out.println(targetObject);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return new User();
    }


    private static JsonNode findObjectWithField(JsonNode rootNode, String field) {
        if (rootNode == null || rootNode.isMissingNode()) {
            return null;
        }

        if (rootNode.has(field)) {
            return rootNode;
        }

        for (JsonNode childNode : rootNode) {
            JsonNode foundNode = findObjectWithField(childNode, field);
            if (foundNode != null) {
                return foundNode;
            }
        }

        return null;
    }



}
