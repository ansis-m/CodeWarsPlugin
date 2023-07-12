package com.example.codewarsplugin.services;

import com.example.codewarsplugin.models.user.User;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.chrome.ChromeDriver;

public class UserService {


    private static ChromeDriver chromeDriver;

    public static User getUser() {
        chromeDriver = WebDriver.getChromeDriver();
        String responseBody = (String) chromeDriver.executeScript(
                "var xhr = new XMLHttpRequest();" +
                        "xhr.open('GET', 'https://www.codewars.com/dashboard', false);" +
                        "xhr.send();" +
                        "return xhr.responseText;"
        );
        System.out.println("Response Body: " + responseBody);

        return new User();
    }
}
