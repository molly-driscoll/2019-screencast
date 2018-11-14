package com.saucelabs.example.stepdefs;

import com.saucelabs.example.DriverFactory;
import com.saucelabs.example.Util;
import com.saucelabs.example.pages.PagesFactory;
import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import cucumber.api.java8.En;
import org.openqa.selenium.remote.BrowserType;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.util.Date;

public class StartingSteps extends DriverFactory implements En
{
    private RemoteWebDriver driver;
    private Date startDate, stopDate;

    public StartingSteps()
    {
        Before((Scenario scenario) -> {
            driver = DriverFactory.getDriverInstance(scenario, BrowserType.CHROME, "65.0", "macOS 10.13");
//            driver = DriverFactory.getDriverInstance(scenario, BrowserType.CHROME, "65.0", "Windows 10");
//            driver = DriverFactory.getDriverInstance(scenario, BrowserType.CHROME, "65.0", "Windows 8.1");
//            driver = DriverFactory.getDriverInstance(scenario, BrowserType.CHROME, "65.0", "Windows 8");
//            driver = DriverFactory.getDriverInstance(scenario, BrowserType.CHROME, "65.0", "Windows 7");
//            driver = DriverFactory.getDriverInstance(scenario, BrowserType.CHROME, "70.0", "Windows 10");
//            driver = DriverFactory.getDriverInstance(scenario, BrowserType.EDGE, "17", "Windows 10");
//            driver = DriverFactory.getDriverInstance(scenario, BrowserType.FIREFOX, "60.0", "Windows 7");

//            driver = DriverFactory.getMobileDriverInstance("iPhone 8", "iOS", "11.3");
//            driver = DriverFactory.getMobileDriverInstance("iPhone 6", "iOS", "11.3");
//            driver = DriverFactory.getMobileDriverInstance("Google Pixel 2 XL", BrowserType.ANDROID, "8.1");


            startDate = new Date();

            String sessionId = driver.getSessionId().toString();
            Util.log(this, "Started %s, session ID=%s.\n", startDate, sessionId);

            PagesFactory.start(driver);
        });

        After((Scenario scenario) -> {
            boolean isSuccess = !scenario.isFailed();

            stopDate = new Date();
            Util.log(this, "Completed %s, %d seconds.\n", stopDate, (stopDate.getTime() - startDate.getTime()) / 1000L);
            Util.reportSauceLabsResult(driver, isSuccess);

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
