package com.example.codewarsplugin.services.katas;

import com.example.codewarsplugin.models.kata.KataRecord;
import com.example.codewarsplugin.services.login.WebDriver;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.chrome.ChromeDriver;

import static com.example.codewarsplugin.config.StringConstants.KATA_URL;
import static com.example.codewarsplugin.services.UserService.findObjectWithField;

public class KataDescriptionService {


    private static ChromeDriver chromeDriver;
    private static String description;
    private static ObjectMapper objectMapper = new ObjectMapper();

    public static void getDescription(KataRecord record) {

        chromeDriver = WebDriver.getChromeDriver();
        chromeDriver.get(String.format(KATA_URL + "/%s/java", record.getId()));
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {}
        String serializedObjects = (String) chromeDriver.executeScript(
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
            JsonNode targetObject = findObjectWithField(rootNode, "challengeName");
            System.out.println("object: " + new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(targetObject));
            //description = objectMapper.treeToValue(targetObject, String.class);
            record.setDescription(targetObject.get("description").asText());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
