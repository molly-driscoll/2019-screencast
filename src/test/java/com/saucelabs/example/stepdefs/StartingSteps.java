package com.saucelabs.example.stepdefs;

import com.saucelabs.example.Browser;
import com.saucelabs.example.DriverFactory;
import com.saucelabs.example.TestPlatform;
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

            TestPlatform tp = Util.getTestPlatform();

            // When running in Intellij as a Cucumber Test (not via TestNG or Maven), the AbstractTestRunner.beforeClass()
            // won't get called and that's where we get out Test Platform info from.  So we set a default test platform
            // in these cases.
            if (tp == null)
            {
                TestPlatform.Builder builder = new TestPlatform.Builder();

                tp = builder.browser(Browser.CHROME).browserVersion("70").platformName("Windows 10").build();
                Util.setTestPlatform(tp);
            }

            driver = DriverFactory.getDriverInstance(tp, scenario);
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

            if (!Util.isMobile || Util.isEmuSim)
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
