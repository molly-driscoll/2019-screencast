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
    @Test(dataProvider = "desktopDataProvider")
    public void verifyValidUsersCanSignIn(String browser, String browserVersion, String os, Method method)
    throws MalformedURLException
    {
        RemoteWebDriver driver = createDesktopDriver(browser, browserVersion, os, method.getName());

        LoginPage loginPage = new LoginPage(driver);
        InventoryPage inventoryPage = new InventoryPage(driver);

        loginPage.navigateTo(LoginPage.PAGE_URL);
        loginPage.enterUsername("standard_user");
        loginPage.enterPassword("secret_sauce");

        loginPage.clickLogin();
        inventoryPage.waitForPageLoad();

        JavascriptExecutor jsExec = (JavascriptExecutor)driver;
        jsExec.executeScript("sauce:context=>>> Verify we are on the Inventory Page");
        
        String currentUrl = driver.getCurrentUrl();
        Assert.assertEquals(currentUrl, InventoryPage.PAGE_URL, "Current URL does not match Expected");

        jsExec.executeScript("sauce:job-result=true");
        driver.quit();
    }
}
