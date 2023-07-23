package com.example.codewarsplugin.services.login;

import io.github.bonigarcia.wdm.WebDriverManager;
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
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                chromeDriver.manage().deleteAllCookies();
                chromeDriver.get("https://www.codewars.com/users/sign_in");
                return null;
            }
        };
        worker.execute();
    }
}
