package com.saucelabs.example.features;

import com.saucelabs.example.pages.InventoryPage;
import com.saucelabs.example.pages.LoginPage;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;

public class LoginFeatures extends BaseFeature
{
    @BeforeTest
    public void setup()
    {
    }

    @Test
    public void verifyValidUsersCanSignIn()
//    @Test(dataProvider = "desktopDataProvider")
//    public void verifyValidUsersCanSignIn(String browser, String browserVersion, String os, Method method)
    throws MalformedURLException
    {
//        WebDriver driver = new ChromeDriver();
        MutableCapabilities caps = MutableCapabilities();
	caps.setCapability("browserName", "chrome");
        caps.setCapability("platform", "Windows 10");
        caps.setCapability("version", "73.0");

        caps.setCapability("username", System.getenv("SAUCE_USERNAME"));
        caps.setCapability("accesskey", System.getenv("SAUCE_ACCESS_KEY"));
        caps.setCapability("name", "Verify Valid Users Can Sign In");

        caps.setCapability("extendedDebugging", true);
        caps.setCapability("capturePerformance", true);

        caps.setCapability("build", "42");

        RemoteWebDriver driver = new RemoteWebDriver(new URL("https://ondemand.saucelabs.com:443/wd/hub"), caps);
//        RemoteWebDriver driver = createDesktopDriver(browser, browserVersion, os, method.getName());

        LoginPage loginPage = new LoginPage(driver);
        InventoryPage inventoryPage = new InventoryPage(driver);

        loginPage.navigateTo(LoginPage.PAGE_URL);
        loginPage.enterUsername("standard_user");
        loginPage.enterPassword("secret_sauce");

        loginPage.clickLogin();
        inventoryPage.waitForPageLoad();

//        JavascriptExecutor jsExec = (JavascriptExecutor)driver;
//        jsExec.executeScript("sauce:context=>>> Verify we are on the Inventory Page");
        String currentUrl = driver.getCurrentUrl();
        Assert.assertEquals(currentUrl, InventoryPage.PAGE_URL, "Current URL does not match Expected");

       Set<String> logTypes = driver.manage().logs().getAvailableLogTypes();
       for (String logType : logTypes)
       {
           System.out.printf("Log Type: %s\n", logType);

           LogEntries logEntries = driver.manage().logs().get(logType);
           for (LogEntry le : logEntries)
           {
               System.out.println(le);
           }
       }

//        jsExec.executeScript("sauce:job-result=true");
        driver.quit();
    }

//    @Test
//    public void verifyLockedOutUserGetsLockedOutMessage()
//    @Test(dataProvider = "desktopDataProvider")
    public void verifyLockedOutUserGetsLockedOutMessage(String browser, String browserVersion, String os, Method method)
    {
//        WebDriver driver = new ChromeDriver();
        RemoteWebDriver driver = createDesktopDriver(browser, browserVersion, os, "Verify Locked Out User Gets Locked Out Message");

        LoginPage loginPage = new LoginPage(driver);
        InventoryPage inventoryPage = new InventoryPage(driver);

        loginPage.navigateTo(LoginPage.PAGE_URL);
        loginPage.enterUsername("locked_out_user");
        loginPage.enterPassword("secret_sauce");

        loginPage.clickLogin();

        JavascriptExecutor jsExec = (JavascriptExecutor)driver;
        jsExec.executeScript("sauce:context=>>> The user should be shown a locked out message");
        Assert.assertTrue(loginPage.hasLockedOutError());

        jsExec.executeScript("sauce:job-result=" + loginPage.hasLockedOutError());
        driver.quit();
    }
}
