package com.saucelabs.example.stepdefs;

import com.saucelabs.example.Util;
import cucumber.api.Scenario;
import cucumber.api.java8.En;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.remote.IOSMobileCapabilityType;
import io.appium.java_client.remote.MobileBrowserType;
import io.appium.java_client.remote.MobileCapabilityType;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.Platform;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DriverFactory implements En
{
    protected static final String userName = System.getenv("SAUCE_USERNAME");
    protected static final String accessKey = System.getenv("SAUCE_ACCESS_KEY");
    protected static final String toAccessKey = System.getenv("TESTOBJECT_API_KEY");

    private static URL LOCAL_URL;
    private static URL SAUCE_URL;
    private static URL TESTOBJECT_URL;

    public enum Browser
    {
        FIREFOX("firefox"), CHROME("chrome"), SAFARI("safari"), EDGE("MicrosoftEdge"), INTERNETEXPLORER("internet explorer");

        private String browserName;

        Browser(String browserName)
        {
            this.browserName = browserName;
        }

        public String toString()
        {
            return browserName;
        }
    }

    static
    {
        try
        {
            LOCAL_URL = new URL("http://127.0.0.1:4723/wd/hub");
        }
        catch (MalformedURLException e)
        {
            System.err.printf("Malformed LOCAL_URL: %s\n", e.getMessage());
            System.exit(-1);
        }

        try
        {
            SAUCE_URL = new URL("https://ondemand.saucelabs.com:443/wd/hub");
        }
        catch (MalformedURLException e)
        {
            System.err.printf("Malformed SAUCE_URL: %s\n", e.getMessage());
            System.exit(-1);
        }

        try
        {
            TESTOBJECT_URL = new URL("https://us1.appium.testobject.com/wd/hub");
        }
        catch (MalformedURLException e)
        {
            System.err.printf("Malformed TESTOBJECT_URL: %s\n", e.getMessage());
            System.exit(-1);
        }
    }

    public static RemoteWebDriver getDesktopDriverInstance(Scenario scenario, Browser browser, String version)
    {
        return getDesktopDriverInstance(scenario, browser, version, "");
    }

    public static RemoteWebDriver getDesktopDriverInstance(Scenario scenario, Browser browser, String version, String platform)
    {
        MutableCapabilities caps = new MutableCapabilities();
        caps.setCapability("browserName", browser.browserName);
        caps.setCapability("platform", platform);
        caps.setCapability("version", version);
        caps.setCapability("recordVideo", "true");
        caps.setCapability("recordMp4", "true");
        caps.setCapability("recordScreenshots", "true");
        caps.setCapability("screenResolution", "1600x1200");
        caps.setCapability("name", scenario.getName());

        RemoteWebDriver driver;
        if (Util.runLocal)
        {
            switch (browser)
            {
                case CHROME:
                    ChromeOptions chromeOptions = new ChromeOptions();
                    chromeOptions.merge(caps);
                    driver = new ChromeDriver(chromeOptions);
                    break;

                case EDGE:
                    EdgeOptions edgeOptions = new EdgeOptions();
                    edgeOptions.merge(caps);
                    driver = new EdgeDriver(edgeOptions);
                    break;

                case FIREFOX:
                    FirefoxOptions firefoxOptions = new FirefoxOptions();
                    firefoxOptions.merge(caps);
                    driver = new FirefoxDriver(firefoxOptions);
                    break;

                case INTERNETEXPLORER:
                    InternetExplorerOptions ieOptions = new InternetExplorerOptions();
                    ieOptions.merge(caps);
                    driver = new InternetExplorerDriver(ieOptions);
                    break;

                case SAFARI:
                    SafariOptions safariOptions = new SafariOptions();
                    safariOptions.merge(caps);
                    driver = new SafariDriver(safariOptions);
                    break;

                default:
                    throw new RuntimeException("Unsupported browser: " + browser);
            }
        }
        else
        {
            caps.setCapability("username", userName);
            caps.setCapability("accesskey", accessKey);
            caps.setCapability("extendedDebugging", true);
            caps.setCapability("build", Util.buildTag);

            driver = new RemoteWebDriver(SAUCE_URL, caps);
        }

        String sessionId = driver.getSessionId().toString();
        Util.log("Started %s", new Date().toString());
        Util.log("Test Results: https://app.saucelabs.com/tests/%s", sessionId);

        // Set reasonable page load and script timeouts
        driver.manage().timeouts().pageLoadTimeout(15, TimeUnit.SECONDS);
        driver.manage().timeouts().setScriptTimeout(15, TimeUnit.SECONDS);

        return driver;
    }

    public static RemoteWebDriver getLocalIOSDriverInstance(Scenario scenario, String deviceName, String xcodeOrgId, String xcodeSigningId, String udid)
    {
        if (Util.runLocal == false)
        {
            throw new RuntimeException("getLocalIOSDriverInstance() called when runLocal set to false");
        }

        Util.isMobile = true;

        MutableCapabilities addlCaps = new MutableCapabilities();

        addlCaps.setCapability("updatedWDABundleId", "io.billmeyer.WebDriverAgentRunner");
        addlCaps.setCapability(MobileCapabilityType.AUTOMATION_NAME, "XCUITest");
        addlCaps.setCapability(IOSMobileCapabilityType.XCODE_ORG_ID, xcodeOrgId);
        addlCaps.setCapability(IOSMobileCapabilityType.XCODE_SIGNING_ID, xcodeSigningId);
        addlCaps.setCapability(MobileCapabilityType.UDID, udid);

        return getMobileDriverInstance(scenario, Platform.IOS, null, deviceName, addlCaps);
    }

    public static RemoteWebDriver getLocalAndroidDriverInstance(Scenario scenario, String deviceName)
    {
        if (Util.runLocal == false)
        {
            throw new RuntimeException("getLocalAndroidDriverInstance() called when runLocal set to false");
        }

        Util.isMobile = true;

        MutableCapabilities addlCaps = new MutableCapabilities();
        addlCaps.setCapability(MobileCapabilityType.AUTOMATION_NAME, "Espresso");

        return getMobileDriverInstance(scenario, Platform.ANDROID, null, deviceName, addlCaps);
    }

    public static RemoteWebDriver getMobileDriverInstance(Scenario scenario, Platform platform, String platformVersion, String deviceName)
    {
        return getMobileDriverInstance(scenario, platform, platformVersion, deviceName, null);
    }

    public static RemoteWebDriver getMobileDriverInstance(Scenario scenario, Platform platform, String platformVersion, String deviceName, MutableCapabilities addlCaps)
    {
        URL url = null;
        RemoteWebDriver driver;

        Util.isMobile = true;

        MutableCapabilities caps = new MutableCapabilities();
        caps.setCapability("name", scenario.getName());

        if (addlCaps != null)
        {
            caps.merge(addlCaps);
        }

        String platformName = platform.getPartOfOsName()[0];
        caps.setCapability("platformName", platformName);

        if (platformVersion != null)
        {
            caps.setCapability("platformVersion", platformVersion);
        }

        if (deviceName != null)
        {
            caps.setCapability("deviceName", deviceName);
        }

        if (Util.runLocal == true)
        {
            url = LOCAL_URL;
        }
        else
        {
            caps.setCapability("appiumVersion", "1.9.1");
            caps.setCapability("deviceOrientation", "portrait");
            caps.setCapability("recordMp4", "true");

            if (deviceName != null && (deviceName.endsWith(" Simulator") || deviceName.endsWith(" Emulator")))
            {
                caps.setCapability("username", userName);
                caps.setCapability("accesskey", accessKey);

                url = SAUCE_URL;
            }
            else
            {
                caps.setCapability("testobject_api_key", toAccessKey);
                url = TESTOBJECT_URL;
            }
        }

        Util.log("Loading driver...");
        long start = System.currentTimeMillis();

        switch (platform)
        {
            case ANDROID:
                caps.setCapability(MobileCapabilityType.BROWSER_NAME, MobileBrowserType.CHROME);
                driver = new AndroidDriver(url, caps);
                break;

            case IOS:
                caps.setCapability(MobileCapabilityType.BROWSER_NAME, MobileBrowserType.SAFARI);
                driver = new IOSDriver(url, caps);

//                driver.executeScript("env.sendKeyStrategy = 'setValue'");
//                driver.executeScript("env.sendKeyStrategy = 'grouped'");
                break;

            default:
                return null;
        }

        long stop = System.currentTimeMillis();
        Util.log("Driver loaded in %.2f seconds", ((stop - start) / 1000f));

        Util.log("Started %s", new Date().toString());
//        String sessionId = driver.getSessionId().toString();
//        Util.log("Test Results: https://app.saucelabs.com/tests/%s", sessionId);

        // Set reasonable page load and script timeouts
        driver.manage().timeouts().pageLoadTimeout(15, TimeUnit.SECONDS);
        driver.manage().timeouts().setScriptTimeout(15, TimeUnit.SECONDS);

//        String jsonCaps = new Gson().toJson(driver.getCapabilities().asMap());
//        Util.log("Capabilities: %s\n", jsonCaps);

        return driver;
    }

}
