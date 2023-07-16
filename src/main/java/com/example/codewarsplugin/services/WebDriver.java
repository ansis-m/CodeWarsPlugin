package com.example.codewarsplugin.services;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;

import javax.swing.*;

public class WebDriver {

    public static ChromeDriver chromeDriver;

    public static void init() {

        if (chromeDriver != null) {
            System.out.println("\n\n\nNOT NULL\n\n\n");
            return;
        }

        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() {
                WebDriverManager.chromedriver().setup();
                ChromeOptions options = new ChromeOptions();
                options.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
                options.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, true);
//                    //options.addArguments("--headless");
                options.addArguments("--disable-notifications");
                chromeDriver = new ChromeDriver(options);
                chromeDriver.get("https://www.codewars.com/users/sign_in");
                return null;
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
}
