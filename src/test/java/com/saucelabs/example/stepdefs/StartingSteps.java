package com.saucelabs.example.stepdefs;

import com.saucelabs.DriverFactory;
import com.saucelabs.example.Util;
import com.saucelabs.example.pages.PagesFactory;
import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java8.En;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.util.Date;

public class StartingSteps extends DriverFactory implements En
{
    private RemoteWebDriver driver;
    private Date startDate, stopDate;

    public StartingSteps()
    {
        Before((Scenario scenario) -> {

            startDate = new Date();

            driver = DriverFactory.getDesktopDriverInstance(scenario, Browser.CHROME, "69.0", "macOS 10.13");
//            driver = DriverFactory.getDesktopDriverInstance(scenario, Browser.FIREFOX, "63.0", "macOS 10.13");
//            driver = DriverFactory.getMobileDriverInstance(scenario, Platform.ANDROID, "9", null);


//            driver = DriverFactory.getDesktopDriverInstance(scenario, BrowserType.CHROME, "65.0", "Windows 10");
//            driver = DriverFactory.getDesktopDriverInstance(scenario, BrowserType.CHROME, "65.0", "Windows 8.1");
//            driver = DriverFactory.getDesktopDriverInstance(scenario, BrowserType.CHROME, "65.0", "Windows 8");
//            driver = DriverFactory.getDesktopDriverInstance(scenario, BrowserType.CHROME, "65.0", "Windows 7");
//            driver = DriverFactory.getDesktopDriverInstance(scenario, BrowserType.CHROME, "70.0", "Windows 10");
//            driver = DriverFactory.getDesktopDriverInstance(scenario, BrowserType.EDGE, "17", "Windows 10");
//            driver = DriverFactory.getDesktopDriverInstance(scenario, BrowserType.FIREFOX, "60.0", "Windows 7");

//            driver = DriverFactory.getMobileDriverInstance(scenario, Platform.IOS, null, null);
//            driver = DriverFactory.getLocalIOSDriverInstance(scenario, "iPhone 7 Plus","ZCSD2ECK77", "iPhone Developer", "6c6d6b2383ecc31680c4df44b9fdb9ba5364535e");
//            driver = DriverFactory.getLocalAndroidDriverInstance(scenario, "Nexus 6P");
//            driver = DriverFactory.getMobileDriverInstance(scenario, Platform.IOS, "12.1", "iPhone 8");
//            driver = DriverFactory.getMobileDriverInstance(scenario, Platform.ANDROID, "9", null);

//            driver = DriverFactory.getMobileDriverInstance(scenario, "iPhone 6", "iOS", "11.3");
//            driver = DriverFactory.getMobileDriverInstance(scenario, "Google Pixel 2 XL", BrowserType.ANDROID, "9");
//            driver = DriverFactory.getMobileDriverInstance(scenario, "iPhone X Simulator", Platform.IOS, "11.3");

            PagesFactory.start(driver);
        });

        After((Scenario scenario) -> {
            boolean isSuccess = !scenario.isFailed();

            stopDate = new Date();
            Util.log("Completed %s, %d seconds.", stopDate, (stopDate.getTime() - startDate.getTime()) / 1000L);

            if (driver == null)
            {
                return;
            }

            if (Util.isMobile == false)
            {
                Util.reportSauceLabsResult(driver, isSuccess);
            }
            else
            {
                String sessionId = driver.getSessionId().toString();
                Util.reportTestObjectResult(sessionId, isSuccess);
            }

            driver.quit();
        });

    }

    @Before("@Signup-DataDriven")
    public void signupSetup()
    {
        System.out.println("This should run everytime before any of the @Signup-DataDriven tagged scenario is going to run");
    }

    @After("@Signup-DataDriven")
    public void signupTeardown()
    {
        System.out.println("This should run everytime after any of the @Signup-DataDriven tagged scenario has run");
    }
}
