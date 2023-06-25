package com.example.codewarsplugin.services;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import javax.swing.*;
import java.util.HashSet;
import java.util.Set;


public class CookieService {


    private static CookieService cookieService = new CookieService();
    private static Set<Cookie> allCookies = new HashSet<>();
    private static WebDriver driver;
    private CookieService(){

    }
    public static CookieService getInstance(){
        if (cookieService == null){
            cookieService = new CookieService();
        }
        WebDriverManager.chromedriver().setup();
        return cookieService;
    }

    public static void login() throws InterruptedException {



        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {

                WebDriverManager.chromedriver().setup();
                ChromeOptions options = new ChromeOptions();
                options.addArguments("--headless");
                driver = new ChromeDriver(options);
                driver.get("https://www.codewars.com/users/sign_in");
                WebElement inputElement = driver.findElement(By.id("user_email"));
                inputElement.clear();
                inputElement.sendKeys("maleckisansis@gmail.com");
                inputElement = driver.findElement(By.id("user_password"));
                inputElement.clear();
                inputElement.sendKeys("Eloraps1!");
                WebElement buttonElement = driver.findElement(By.className("btn"));
                buttonElement.click();

                allCookies.addAll(driver.manage().getCookies());
                quit();
                return null;
            }

            @Override
            protected void done() {
                // Update UI or perform any other tasks after processing is complete
                SwingUtilities.invokeLater(() -> {
                    System.out.println("Invoke Later!!!");
                });
            }
        };

        worker.execute();
    }

    public static void quit(){
        driver.quit();
        driver = null;
    }

    public static Set<Cookie> getAllCookies(){
        return allCookies;
    }

    public WebDriver getWebDriver() throws InterruptedException {
        if (driver == null){
            login();
        }
        return driver;
    }
}
