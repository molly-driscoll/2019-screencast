package com.saucelabs;

import com.saucelabs.example.TestPlatform;
import com.saucelabs.example.Util;
import cucumber.api.Scenario;
import cucumber.api.java8.En;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
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
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DriverFactory implements En
{
    private static final String userName = System.getenv("SAUCE_USERNAME");
    private static final String accessKey = System.getenv("SAUCE_ACCESS_KEY");
    private static final String toAccessKey = System.getenv("TESTOBJECT_API_KEY");

    private static URL LOCAL_SELENIUM_URL;
    private static URL LOCAL_APPIUM_URL;
    private static URL SAUCE_URL;
    private static URL TESTOBJECT_URL;

    static
    {
        try
        {
            LOCAL_SELENIUM_URL = new URL("http://127.0.0.1:4444/wd/hub");
        }
        catch (MalformedURLException e)
        {
            System.err.printf("Malformed LOCAL_APPIUM_URL: %s\n", e.getMessage());
            System.exit(-1);
        }

        try
        {
            LOCAL_APPIUM_URL = new URL("http://127.0.0.1:4723/wd/hub");
        }
        catch (MalformedURLException e)
        {
            System.err.printf("Malformed LOCAL_APPIUM_URL: %s\n", e.getMessage());
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

    public static RemoteWebDriver getDriverInstance(TestPlatform tp, Scenario scenario)
    {
        RemoteWebDriver driver = null;

        String platform = tp.getPlatformName();
        if (platform.startsWith("Windows ") || platform.startsWith("macOS ") || platform.startsWith("OS X"))
        {
            driver = getDesktopDriverInstance(tp, scenario);
        }
        else if (platform.equals("iOS") || platform.equals("Android"))
        {
            driver = DriverFactory.getMobileDriverInstance(tp, scenario, null);
        }

        return driver;
    }

    private static RemoteWebDriver getDesktopDriverInstance(TestPlatform tp, Scenario scenario)
    {
        MutableCapabilities caps = new MutableCapabilities();
        caps.setCapability("browserName", tp.getBrowser().toString());
        caps.setCapability("version", tp.getBrowserVersion());
        caps.setCapability("platform", tp.getPlatformName());
        caps.setCapability("name", scenario.getName());

        // Set ACCEPT_SSL_CERTS  variable to true
        caps.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);

        RemoteWebDriver driver = null;
        if (Util.runLocal)
        {
            switch (tp.getBrowser())
            {
                case CHROME:
                    ChromeOptions chromeOptions = new ChromeOptions();
                    chromeOptions.addArguments("--ignore-certificate-errors");
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
                    firefoxOptions.setCapability("marionette", false);
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
                    throw new RuntimeException("Unsupported browserName: " + tp.getBrowser());
            }
            driver.manage().window().maximize();
        }
        else
        {
            caps.setCapability("username", userName);
            caps.setCapability("accesskey", accessKey);
            caps.setCapability("recordVideo", "true");
            caps.setCapability("recordMp4", "true");
            caps.setCapability("recordScreenshots", "true");
//            caps.setCapability("screenResolution", "1600x1200");

//            if (browserName == Browser.CHROME)
            caps.setCapability("extendedDebugging", true);

            // Pull the Job Name and Build Number from Jenkins if available...
            String jenkinsBuildNumber = System.getenv("JENKINS_BUILD_NUMBER");
            if (jenkinsBuildNumber != null)
            {
                caps.setCapability("build", jenkinsBuildNumber);
            }
            else
            {
                String jobName = System.getenv("JOB_NAME");
                String buildNumber = System.getenv("BUILD_NUMBER");

                if (jobName != null && buildNumber != null)
                {
                    caps.setCapability("build", String.format("%s__%s", jobName, buildNumber));
                }
                else
                {
                    caps.setCapability("build", Util.buildTag);
                }
            }

//            URL url = null;
//            try
//            {
//                url = new URL("https://ondemand.us-east1.headless.saucelabs.com/wd/hub");
//            }
//            catch (MalformedURLException e)
//            {
//                e.printStackTrace();
//            }
//
//            AllTrustingHttpClientFactory clientFactory = new AllTrustingHttpClientFactory();
//
//            HttpCommandExecutor executor = new HttpCommandExecutor(ImmutableMap.of(), url, clientFactory);
//            driver = new RemoteWebDriver(executor, caps);

            driver = new RemoteWebDriver(SAUCE_URL, caps);
        }

        String sessionId = driver.getSessionId().toString();
        Util.log("Started %s", new Date().toString());
        Util.log("Test Results: https://app.saucelabs.com/tests/%s", sessionId);
        Util.log("SauceOnDemandSessionID=%s job-name=%s", sessionId, scenario.getName());

        // Set reasonable page load and script timeouts
//        driver.manage().timeouts().pageLoadTimeout(15, TimeUnit.SECONDS);
//        driver.manage().timeouts().setScriptTimeout(15, TimeUnit.SECONDS);

        return driver;
    }

//    private static RemoteWebDriver getLocalIOSDriverInstance(Scenario scenario, String deviceName, String xcodeOrgId,
//                                                             String xcodeSigningId, String udid)
//    {
//        if (Util.runLocal == false)
//        {
//            throw new RuntimeException("getLocalIOSDriverInstance() called when runLocal set to false");
//        }
//
//        Util.isMobile = true;
//
//        MutableCapabilities addlCaps = new MutableCapabilities();
//
//        addlCaps.setCapability("updatedWDABundleId", "io.billmeyer.WebDriverAgentRunner");
//        addlCaps.setCapability(MobileCapabilityType.AUTOMATION_NAME, "XCUITest");
//        addlCaps.setCapability(IOSMobileCapabilityType.XCODE_ORG_ID, xcodeOrgId);
//        addlCaps.setCapability(IOSMobileCapabilityType.XCODE_SIGNING_ID, xcodeSigningId);
//        addlCaps.setCapability(MobileCapabilityType.UDID, udid);
//
//        return getMobileDriverInstance(scenario, Platform.IOS, null, deviceName, addlCaps);
//    }

//    private static RemoteWebDriver getLocalAndroidDriverInstance(Scenario scenario, String deviceName)
//    {
//        if (Util.runLocal == false)
//        {
//            throw new RuntimeException("getLocalAndroidDriverInstance() called when runLocal set to false");
//        }
//
//        Util.isMobile = true;
//
//        MutableCapabilities addlCaps = new MutableCapabilities();
//        addlCaps.setCapability(MobileCapabilityType.AUTOMATION_NAME, "Espresso");
//
//        return getMobileDriverInstance(scenario, Platform.ANDROID, null, deviceName, addlCaps);
//    }

    private static RemoteWebDriver getMobileDriverInstance(TestPlatform tp, Scenario scenario,
                                                           MutableCapabilities addlCaps)
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

        caps.setCapability("platformName", tp.getPlatformName());

        if (tp.getPlatformVersion() != null)
        {
            caps.setCapability("platformVersion", tp.getPlatformVersion());
        }

        if (tp.getDeviceName() != null)
        {
            caps.setCapability("deviceName", tp.getDeviceName());
        }

        if (Util.runLocal == true)
        {
            url = LOCAL_APPIUM_URL;
        }
        else
        {
            caps.setCapability("appiumVersion", "1.9.1");
            caps.setCapability("deviceOrientation", "portrait");
            caps.setCapability("recordMp4", "true");

            if (tp.getDeviceName() != null && (tp.getDeviceName().endsWith(" Simulator") || tp.getDeviceName().endsWith(
                    " Emulator")))
            {
                Util.isEmuSim = true;
                caps.setCapability("username", userName);
                caps.setCapability("accesskey", accessKey);

                url = SAUCE_URL;
            }
            else
            {
                Util.isEmuSim = false;
                caps.setCapability("testobject_api_key", toAccessKey);
                url = TESTOBJECT_URL;
            }
        }

        Util.log("Loading driver...");
        long start = System.currentTimeMillis();

        Platform platform = Platform.fromString(tp.getPlatformName());
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
