package com.saucelabs.example.features;

import com.saucelabs.example.Util;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import io.appium.java_client.remote.MobileBrowserType;
import io.appium.java_client.remote.MobileCapabilityType;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.Platform;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.DataProvider;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public abstract class BaseFeature
{
    private static String userName = System.getenv("SAUCE_USERNAME");
    private static String accessKey = System.getenv("SAUCE_ACCESS_KEY");
    private static String tunnelId = System.getenv("SAUCE_TUNNEL_ID");
    private static final String toAccessKey = System.getenv("TESTOBJECT_API_KEY");
    private static final String headlessUserName = System.getenv("HEADLESS_SAUCE_USERNAME");
    private static final String headlessAccessKey = System.getenv("HEADLESS_SAUCE_ACCESS_KEY");

    private static URL SAUCE_US_URL;
    private static URL SAUCE_EU_URL;
    private static URL TESTOBJECT_URL;
    private static URL HEADLESS_URL;

    static
    {
        try
        {
            SAUCE_US_URL = new URL("https://ondemand.saucelabs.com:443/wd/hub");
        }
        catch (MalformedURLException e)

        {
            System.err.printf("Malformed SAUCE_US_URL: %s\n", e.getMessage());
            System.exit(-1);
        }

        try

        {
            SAUCE_EU_URL = new URL("https://ondemand.eu-central-1.saucelabs.com:443/wd/hub");
        }
        catch (MalformedURLException e)

        {
            System.err.printf("Malformed SAUCE_EU_URL: %s\n", e.getMessage());
            System.exit(-1);
        }

        try

        {
            TESTOBJECT_URL = new URL("http://us1.appium.testobject.com/wd/hub");
        }
        catch (MalformedURLException e)

        {
            System.err.printf("Malformed TESTOBJECT_URL: %s\n", e.getMessage());
            System.exit(-1);
        }

        try

        {
            HEADLESS_URL = new URL("http://ondemand.us-east1.headless.saucelabs.com/wd/hub");
        }
        catch (MalformedURLException e)

        {
            System.err.printf("Malformed HEADLESS_URL: %s\n", e.getMessage());
            System.exit(-1);
        }

    }

//    @DataProvider(name = "mobileDataProvider", parallel = true)
    public static Object[][] mobileDataProvider(Method method)
    {
        return new Object[][] {
                new Object[] { "Android", "9", "Google.*" },
                new Object[] { "Android", "8.1.0", ".*" },
                new Object[] { "Android", "8.1", "Android GoogleAPI Emulator" },
                new Object[] { "Android", "8.0", "Android GoogleAPI Emulator" },
                new Object[] { "Android", "7.1", "Android GoogleAPI Emulator" }
        };
    }

    @DataProvider(name = "desktopDataProvider", parallel = true)
    public static Object[][] desktopDataProvider(Method testMethod)
    {
        ArrayList<Object[]> desktops = new ArrayList<>(50);

        // Browser versions we wish to test on...
        final String[] chromeVers = {"73.0", "72.0"};
        final String[] firefoxVers = {"73.0", "72.0"};
        final String[] safariVers = {"12.0"};
        final String[] edgeVers = {"18.17763", "16.16299", "15.15063"};

        // Operating System versions we wish to test on...
        final String[] macOSVers = {"10.14", "10.13"};
        final String[] winVers = {"10", "8.1", "8", "7"};

        // Build Browser/OS combos for Chrome...
        for (String chromeVer : chromeVers)
        {
            for (String macOSVer : macOSVers)
            {
                desktops.add(new Object[]{"chrome", chromeVer, "macOS " + macOSVer});
            }
            for (String winVer : winVers)
            {
                desktops.add(new Object[]{"chrome", chromeVer, "Windows " + winVer});
            }
        }

        // Build Browser/OS combos for Firefox...
        for (String firefoxVer : firefoxVers)
        {
            for (String macOSVer : macOSVers)
            {
                desktops.add(new Object[]{"chrome", firefoxVer, "macOS " + macOSVer});
            }
            for (String winVer : winVers)
            {
                desktops.add(new Object[]{"chrome", firefoxVer, "Windows " + winVer});
            }
        }

        // Build Browser/OS combos for Safari...
        for (String safariVer : safariVers)
        {
            for (String macOSVer : macOSVers)
            {
                desktops.add(new Object[]{"safari", safariVer, "macOS " + macOSVer});
            }
        }

        // Build Browser/OS combos for Edge...
        for (String edgeVer : edgeVers)
        {
            desktops.add(new Object[]{"microsoftedge", edgeVer, "Windows 10"});
        }

        System.out.printf("Configured %d OS/Browser combos for testing.\n", desktops.size());
        Object[][] desktopsArray = desktops.toArray(new Object[desktops.size()][]);
        return desktopsArray;
    }

    /**
     * Constructs a new {@link RemoteWebDriver} instance which is configured to use the capabilities defined by the
     * browser, browserVersion and os parameters, and which is configured to run against ondemand.saucelabs.com, using
     * the userName and access key populated by the instance.
     *
     * @param browser        Represents the browser to be used as part of the test run.
     * @param browserVersion Represents the browserVersion of the browser to be used as part of the test run.
     * @param os             Represents the operating system to be used as part of the test run.
     * @param testName       Represents the name of the test case that will be used to identify the test on Sauce.
     * @return
     * @throws MalformedURLException if an error occurs parsing the url
     */
    protected RemoteWebDriver createDesktopDriver(String browser, String browserVersion, String os, String testName)
    {
        MutableCapabilities caps = new MutableCapabilities();
        caps.setCapability("browserName", browser);
        caps.setCapability("version", browserVersion);
        caps.setCapability("platform", os);

        caps.setCapability("avoidProxy", true);

        // Build the Sauce Options first...
        MutableCapabilities sauceOpts = new MutableCapabilities();
        sauceOpts.setCapability("name", testName);
        sauceOpts.setCapability("username", System.getenv("SAUCE_USERNAME"));
        sauceOpts.setCapability("accesskey", System.getenv("SAUCE_ACCESS_KEY"));
//        sauceOpts.setCapability("tunnelId", System.getenv("SAUCE_TUNNEL_ID"));

//        sauceOpts.setCapability("recordVideo", "true");
//        sauceOpts.setCapability("recordMp4", "true");
//        sauceOpts.setCapability("recordScreenshots", "true");
//        sauceOpts.setCapability("screenResolution", "1600x1200");
//        sauceOpts.setCapability("extendedDebugging", true);
//        sauceOpts.setCapability("capturePerformance", true);

        caps.merge(sauceOpts);
//        caps.setCapability("sauce:options", sauceOpts);

        // For browsers that support W3C natively, turn it on!
//        switch(browser)
//        {
//            case "chrome":
//                ChromeOptions chromeOptions = new ChromeOptions();
//                chromeOptions.setExperimentalOption("w3c", true);
//                caps.setCapability(ChromeOptions.CAPABILITY, chromeOptions);
//                break;
//
//            case "firefox":
//                FirefoxOptions firefoxOptions = new FirefoxOptions();
//                firefoxOptions.setCapability("w3c", true);
//                caps.setCapability(FirefoxOptions.FIREFOX_OPTIONS, firefoxOptions);
//                break;
//        }

        RemoteWebDriver driver = new RemoteWebDriver(SAUCE_US_URL, caps);
//        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);

        return driver;
    }

    protected static RemoteWebDriver createMobileDriver(String platformName, String platformVersion, String deviceName,
                                                        String testName)
    {
        URL url = null;
        RemoteWebDriver driver;

        MutableCapabilities caps = new MutableCapabilities();
        caps.setCapability("name", testName);

        caps.setCapability("platformName", platformName);

        if (platformVersion != null)
        {
            caps.setCapability("platformVersion", platformVersion);
        }

        if (deviceName != null)
        {
            caps.setCapability("deviceName", deviceName);
        }

        caps.setCapability("appiumVersion", "1.12.1");
        caps.setCapability("deviceOrientation", "portrait");
        caps.setCapability("recordMp4", "true");
        caps.setCapability("browserName", "chrome");

        if (deviceName != null && (deviceName.endsWith(" Simulator") || deviceName.endsWith(" Emulator")))
        {
            Util.isEmuSim = true;
            caps.setCapability("username", userName);
            caps.setCapability("accesskey", accessKey);

            url = SAUCE_US_URL;
        }
        else
        {
            Util.isEmuSim = false;
            caps.setCapability("testobject_api_key", toAccessKey);
            url = TESTOBJECT_URL;
        }

        Util.log("Loading driver...");
        long start = System.currentTimeMillis();

        Platform platform = Platform.fromString(platformName);
        switch (platform)
        {
            case ANDROID:
                caps.setCapability(MobileCapabilityType.BROWSER_NAME, MobileBrowserType.CHROME);
//                caps.setCapability("automationName", "UIAutomator2");
                driver = new AndroidDriver(url, caps);
                break;

            case IOS:
                caps.setCapability(MobileCapabilityType.BROWSER_NAME, MobileBrowserType.SAFARI);
//                caps.setCapability("automationName", "XCUITest");
                driver = new IOSDriver(url, caps);
                break;

            default:
                return null;
        }

        long stop = System.currentTimeMillis();
        Util.log("Driver loaded in %.2f seconds", ((stop - start) / 1000f));

        Util.log("Started %s", new Date().toString());

        // Set reasonable page load and script timeouts
        driver.manage().timeouts().pageLoadTimeout(15, TimeUnit.SECONDS);
        driver.manage().timeouts().setScriptTimeout(15, TimeUnit.SECONDS);

        return driver;
    }
}
