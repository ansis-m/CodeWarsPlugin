package com.example.codewarsplugin.services;

import com.example.codewarsplugin.models.user.User;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.chrome.ChromeDriver;

public class UserService {


    private static ChromeDriver chromeDriver;

    public static User getUser() {
        chromeDriver = WebDriver.getChromeDriver();
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

        ObjectMapper objectMapper = new ObjectMapper();

        try {
            JsonNode rootNode = objectMapper.readTree(serializedObjects);
            JsonNode targetObject = findObjectWithField(rootNode, "career_paths");
            return objectMapper.treeToValue(targetObject, User.class);
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
