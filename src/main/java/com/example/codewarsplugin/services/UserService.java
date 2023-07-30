package com.example.codewarsplugin.services;

import com.example.codewarsplugin.models.user.User;
import com.example.codewarsplugin.services.login.WebDriver;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.chrome.ChromeDriver;

import static com.example.codewarsplugin.config.StringConstants.DASHBOARD_URL;

public class UserService {

    private static ChromeDriver chromeDriver;
    private static User user;
    private static ObjectMapper objectMapper = new ObjectMapper();

    public static User getUser() {
        if(user != null) {
            return user;
        }
        chromeDriver = WebDriver.getChromeDriver();
        if (!chromeDriver.getCurrentUrl().equals(DASHBOARD_URL)){
            chromeDriver.get(DASHBOARD_URL);
        }
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

        try {
            JsonNode rootNode = objectMapper.readTree(serializedObjects);
            JsonNode targetObject = findObjectWithField(rootNode, "career_paths");
            user = objectMapper.treeToValue(targetObject, User.class);
            return user;
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

    public static void clearUser() {
        user = null;
    }
}
