package com.example.codewarsplugin.services.login;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;

import javax.swing.*;
import java.util.concurrent.ExecutionException;

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
//                    //options.addArguments("--headless");
                options.addArguments("--disable-notifications");
                var driver = new ChromeDriver(options);
                driver.get("https://www.codewars.com/users/sign_in");
                return driver;
            }
            @Override
            protected void done(){
                try {
                    chromeDriver = get();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } catch (ExecutionException e) {
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
            chromeDriver.get("https://www.codewars.com/dashboard");
        }
        WebElement anchorElement = chromeDriver.findElement(By.className("js-sign-out"));
        System.out.println("\n\n\n\nElement text: " + anchorElement.getText());
        chromeDriver.executeScript("arguments[0].click();", anchorElement);
        chromeDriver.manage().deleteAllCookies();
        chromeDriver.get("https://www.codewars.com/users/sign_in");
    }
}
