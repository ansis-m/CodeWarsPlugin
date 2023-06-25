package com.example.codewarsplugin.services;

import io.github.bonigarcia.wdm.WebDriverManager;

import net.lightbody.bmp.BrowserMobProxyServer;
import net.lightbody.bmp.core.har.Har;
import net.lightbody.bmp.core.har.HarEntry;
import net.lightbody.bmp.core.har.HarLog;
import net.lightbody.bmp.proxy.CaptureType;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;


import javax.swing.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class LoginService {

    private static LoginService loginService = new LoginService();
    private static Set<Cookie> allCookies = new HashSet<>();
    private static WebDriver driver;
    private static String currentLogin;
    private static String currentPassword;
    private static String csrfToken;
    private static String sessionId;

    private LoginService(){
    }
    public static LoginService getInstance(){
        if (loginService == null){
            loginService = new LoginService();
        }
        return loginService;
    }

    public static boolean login(String login, String password){
        if(notValid(login, password) || !getCookies(login, password)){
            return false;
        }
        currentLogin = login;
        currentPassword = password;

        return true;
    }

    private static boolean getCookies(String login, String password) {

        System.out.println("getCookies");

        try{
            SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() {
                    System.out.println("do in background");
//                    BrowserMobProxyServer proxyServer = new BrowserMobProxyServer();
//                    proxyServer.start();
//                    int proxyPort = proxyServer.getPort();

                    WebDriverManager.chromedriver().setup();
//                    ChromeOptions options = new ChromeOptions();
//                    //options.addArguments("--headless");
//
//
//                    Proxy seleniumProxy = new Proxy();
//                    seleniumProxy.setHttpProxy("localhost:" + proxyPort);
//                    seleniumProxy.setSslProxy("localhost:" + proxyPort);
//                    options.setProxy(seleniumProxy);
                    ChromeOptions options = new ChromeOptions();
                    driver = new ChromeDriver(options);

                    driver.get("https://www.codewars.com/users/sign_in");
                    WebElement inputElement = driver.findElement(By.id("user_email"));
                    inputElement.clear();
                    inputElement.sendKeys(login);
                    inputElement = driver.findElement(By.id("user_password"));
                    inputElement.clear();
                    inputElement.sendKeys(password);
                    WebElement buttonElement = driver.findElement(By.className("btn"));


//                    proxyServer.enableHarCaptureTypes(CaptureType.REQUEST_CONTENT, CaptureType.RESPONSE_CONTENT);
//                    proxyServer.newHar("example");




                    buttonElement.click();



//                    Har har = proxyServer.getHar();

                    // Access the log entries
//                    HarLog log = har.getLog();
//                    List<HarEntry> entries = log.getEntries();
//
//                    // Print the request and response details for each entry
//                    for (HarEntry entry : entries) {
//                        System.out.println("URL: " + entry.getRequest().getUrl());
//                        System.out.println("Method: " + entry.getRequest().getMethod());
//                        System.out.println("Status: " + entry.getResponse().getStatus());
//                        System.out.println("Response Content: " + entry.getResponse().getContent().getText());
//                        System.out.println("------------------------------");
//                    }





                    allCookies = driver.manage().getCookies();

                    allCookies.forEach(cookie -> System.out.println(cookie.getName() + "~~~" + cookie.getValue()));

                    csrfToken = allCookies.stream().filter(cookie -> cookie.getName().contains("CSRF-TOKEN")).findFirst().get().getValue();
                    sessionId = allCookies.stream().filter(cookie -> cookie.getName().contains("session_id")).findFirst().get().getValue();

                    currentPassword = password;
                    currentLogin = login;

                    quit();
                    return null;
                }

                @Override
                protected void done() {
                    SwingUtilities.invokeLater(() -> {
                        System.out.println("Invoke Later!!! " + csrfToken + "\nid: " + sessionId);
                    });
                }
            };

            worker.execute();

            return true;
        } catch (Exception e){
            System.out.println("Exception trying to login: " + e.getMessage());
            return false;
        }
    }

    private static String extractToken(List<String> cookies, String id) {
        return cookies.stream()
                .filter(cookie -> cookie.contains(id))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(id + " not found in set_Cookie!"))
                .split(";")[0].split("=")[1];
    }

    public static String getCsrfToken() {
        return csrfToken;
    }

    public static String getSessionId() {
        return sessionId;
    }

    private static boolean notValid(String login, String password) {
        return !(login != null && password != null && login.length() > 0 && password.length() > 0);
    }

    public static void quit(){
        driver.quit();
        driver = null;
    }
}
