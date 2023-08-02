package com.example.codewarsplugin.services;

import com.example.codewarsplugin.models.user.User;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import static com.example.codewarsplugin.config.StringConstants.DASHBOARD_URL;

public class UserService {

    private static User user;
    private static ObjectMapper objectMapper = new ObjectMapper();


    public static User getUser(){
        return getUser("");
    }


    public static User getUser(String serializedObjects) {
        if(user != null) {
            return user;
        }

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


    public static JsonNode findObjectWithField(JsonNode rootNode, String field) {
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
