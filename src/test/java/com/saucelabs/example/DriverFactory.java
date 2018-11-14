package com.saucelabs.example;

import com.google.gson.Gson;
import cucumber.api.Scenario;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DriverFactory
{
    protected static final String userName = System.getenv("SAUCE_USERNAME");
    protected static final String accessKey = System.getenv("SAUCE_ACCESS_KEY");
    protected static final String toAccessKey = System.getenv("TESTOBJECT_API_KEY");


    public static RemoteWebDriver getDriverInstance(Scenario scenario, String browser, String version)
    {
        return getDriverInstance(scenario, browser, version, "");
    }

    public static RemoteWebDriver getDriverInstance(Scenario scenario, String browser, String version, String platform)
    {
        MutableCapabilities caps = new MutableCapabilities();
        caps.setCapability("browserName", browser);
        caps.setCapability("platform", platform);
        caps.setCapability("version", version);
        caps.setCapability("recordVideo", "true");
        caps.setCapability("recordMp4", "true");
        caps.setCapability("recordScreenshots", "true");
        caps.setCapability("screenResolution", "1600x1200");

        Date startDate = new Date();
//        caps.setCapability("name", String.format("Thrivent - %s [%s]", caps.getBrowserName(), startDate));
        caps.setCapability("name", "Sauce Demo - Find a Financial Representative");
//        caps.setCapability("name", String.format("%s [%s]", scenario.getName(), startDate));
//
        RemoteWebDriver driver;
///
        if (Util.runLocal)
        {
            String browserName = caps.getBrowserName();
            switch (browserName)
            {
                case BrowserType.CHROME:
                    ChromeOptions chromeOptions = new ChromeOptions();
                    chromeOptions.merge(caps);
                    driver = new ChromeDriver(chromeOptions);
                    break;

                case "edge":
                    EdgeOptions edgeOptions = new EdgeOptions();
                    edgeOptions.merge(caps);
                    driver = new EdgeDriver(edgeOptions);
                    break;

                case "firefox":
                    FirefoxOptions firefoxOptions = new FirefoxOptions();
                    firefoxOptions.merge(caps);
                    driver = new FirefoxDriver(firefoxOptions);
                    break;

                case "internet explorer":
                    InternetExplorerOptions ieOptions = new InternetExplorerOptions();
                    ieOptions.merge(caps);
                    driver = new InternetExplorerDriver(ieOptions);
                    break;

                case "safari":
                    SafariOptions safariOptions = new SafariOptions();
                    safariOptions.merge(caps);
                    driver = new SafariDriver(safariOptions);
                    break;

                default:
                    throw new RuntimeException("Unsupported browser: " + browserName);
            }
        }
        else
        {
            caps.setCapability("username", userName);
            caps.setCapability("accesskey", accessKey);
            caps.setCapability("extendedDebugging", true);
            caps.setCapability("build", Util.buildTag);

            URL url = null;
            try
            {
//                url = new URL("http://localhost:4444/wd/hub");
                url = new URL("https://ondemand.saucelabs.com:443/wd/hub");
//                url = new URL("https://162.222.75.33:443/wd/hub");
            }
            catch (MalformedURLException ignored)
            {
            }
            driver = new RemoteWebDriver(url, caps);
        }

        String sessionId = driver.getSessionId().toString();
        Util.log("Started %s, session ID=%s.\n", new Date().toString(), sessionId);

        // Need page load timeout because of lingering request to https://pixel.jumptap.com taking 78+ seconds
        driver.manage().timeouts().pageLoadTimeout(15, TimeUnit.SECONDS);
        driver.manage().timeouts().setScriptTimeout(15, TimeUnit.SECONDS);

        return driver;
    }

    public static RemoteWebDriver getMobileDriverInstance(String deviceName, String platformName, String platformVersion)
    {
        RemoteWebDriver driver;

        Util.isMobile = true;

        MutableCapabilities caps = new MutableCapabilities();
        caps.setCapability("deviceName", deviceName);
        caps.setCapability("platformName", platformName);
        caps.setCapability("platformVersion", platformVersion);

        if (Util.runLocal == true)
        {
            URL url = null;
            try
            {
                url = new URL("http://127.0.0.1:4723/wd/hub");
            }
            catch (MalformedURLException e)
            {
                e.printStackTrace();
            }

            switch (platformName)
            {
                case BrowserType.ANDROID:
                    caps.setCapability("browserName", "Chrome");
                    driver = new AndroidDriver(url, caps);
                    break;

                case "iOS":
                    caps.setCapability("browserName", "Safari");
                    driver = new IOSDriver(url, caps);
                    break;

                default:
                    return null;
            }
        }
        else
        {
            caps.setCapability("testobject_api_key", toAccessKey);
            caps.setCapability("appiumVersion", "1.7.2-cd-2.37");
            caps.setCapability("deviceOrientation", "portrait");
            caps.setCapability("username", userName);
            caps.setCapability("accesskey", accessKey);
            caps.setCapability("recordMp4", "true");

            URL url = null;
            try
            {
                url = new URL("https://us1.appium.testobject.com/wd/hub");
            }
            catch (MalformedURLException ignored)
            {
            }

            switch (platformName)
            {
                case BrowserType.ANDROID:
                    caps.setCapability("browserName", "Chrome");
                    driver = new AndroidDriver(url, caps);
                    break;

                case "iOS":
                    caps.setCapability("browserName", "Safari");
                    driver = new IOSDriver(url, caps);
                    break;

                default:
                    return null;
            }
        }

        String sessionId = driver.getSessionId().toString();
        Util.log("Started %s, session ID=%s.\n", new Date().toString(), sessionId);

        // Need page load timeout because of lingering request to https://pixel.jumptap.com taking 78+ seconds
        driver.manage().timeouts().pageLoadTimeout(15, TimeUnit.SECONDS);
        driver.manage().timeouts().setScriptTimeout(15, TimeUnit.SECONDS);

        String jsonCaps = new Gson().toJson(driver.getCapabilities().asMap());
        Util.log("Capabilities: %s\n", jsonCaps);

        return driver;
    }

}
