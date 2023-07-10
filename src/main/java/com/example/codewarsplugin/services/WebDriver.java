package com.example.codewarsplugin.services;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.CapabilityType;

public class WebDriver {

    public static ChromeDriver chromeDriver;

    public static void init() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
        options.setCapability(CapabilityType.ACCEPT_INSECURE_CERTS, true);
//                    //options.addArguments("--headless");

        chromeDriver = new ChromeDriver(options);
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
