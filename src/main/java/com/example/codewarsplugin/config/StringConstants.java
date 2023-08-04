package com.example.codewarsplugin.config;

public class StringConstants {
    public static final String RECORD_URL = "https://www.codewars.com/api/v1/code-challenges";
    public static final String DASHBOARD_URL = "https://www.codewars.com/dashboard";
    public static final String SIGN_IN_URL = "https://www.codewars.com/users/sign_in";
    public static final String AUTHORIZE_URL = "https://www.codewars.com/api/v1/runner/authorize";
    public static final String KATA_URL = "https://www.codewars.com/kata";
    public static final String FILENAME = "test.json";


    public static final String DASHBOARD = "Dashboard";
    public static final String WORKSPACE = "Workspace";
    public static final String PROJECT = "Project";
    public static final String DESCRIPTION = "Description";
    public static final String ABOUT = "About";



    public static final String SERIALIZE_WINDOW =
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
            "var result = serialize(window);";
}
