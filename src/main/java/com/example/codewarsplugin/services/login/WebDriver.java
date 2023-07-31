package com.example.codewarsplugin.services.login;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;

import javax.swing.*;
import java.util.concurrent.ExecutionException;

import static com.example.codewarsplugin.config.StringConstants.DASHBOARD_URL;
import static com.example.codewarsplugin.config.StringConstants.SIGN_IN_URL;

public class WebDriver {

    public static ChromeDriver chromeDriver;

    public static void init() {

        if (chromeDriver != null) {
            return;
        }

        SwingWorker<ChromeDriver, Void> worker = new SwingWorker<ChromeDriver, Void>() {
            @Override
            protected ChromeDriver doInBackground() {
                WebDriverManager.chromedriver().setup();
                ChromeOptions options = new ChromeOptions();
                options.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
                options.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, true);
                options.addArguments("--headless");
                options.addArguments("--disable-notifications");
                var driver = new ChromeDriver(options);
                driver.get(SIGN_IN_URL);
                return driver;
            }
            @Override
            protected void done(){
                try {
                    chromeDriver = get();
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }
        };
        worker.execute();

    }
    public static ChromeDriver getChromeDriver() {
        if (chromeDriver == null) {
            init();
        }
        return chromeDriver;
    }

    public static void quit(){
        chromeDriver.quit();
        chromeDriver = null;
    }

    public static void logout(){
        if (!chromeDriver.getCurrentUrl().equals("")) {
            chromeDriver.get(DASHBOARD_URL);
        }
        WebElement anchorElement = chromeDriver.findElement(By.className("js-sign-out"));
        chromeDriver.executeScript("arguments[0].click();", anchorElement);
        chromeDriver.manage().deleteAllCookies();
        chromeDriver.get(SIGN_IN_URL);
    }
}
