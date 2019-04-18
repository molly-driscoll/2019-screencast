package com.saucelabs.example.features;

import com.saucelabs.example.Util;
import com.saucelabs.example.pages.InventoryPage;
import com.saucelabs.example.pages.LoginPage;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.lang.reflect.Method;

public class MobileLoginFeatures extends BaseFeature
{
    @BeforeTest
    public void setup()
    {
    }

//    @Test
//    public void verifyValidUsersCanSignIn()
    @Test(dataProvider = "mobileDataProvider")
    public void verifyValidUsersCanSignIn(String platformName, String platformVersion, String deviceName, Method method)
    {
//        WebDriver driver = new ChromeDriver();
        RemoteWebDriver driver = createMobileDriver(platformName, platformVersion, deviceName, "Verify Valid Users Can Sign In");

        LoginPage loginPage = new LoginPage(driver);
        InventoryPage inventoryPage = new InventoryPage(driver);

        loginPage.navigateTo(LoginPage.PAGE_URL);
        loginPage.enterUsername("standard_user");
        loginPage.enterPassword("secret_sauce");

        loginPage.clickLogin();
        inventoryPage.waitForPageLoad();

        Util.info(driver, ">>> Verify we are on the Inventory Page");
        String currentUrl = driver.getCurrentUrl();
        Assert.assertEquals(currentUrl, InventoryPage.PAGE_URL, "Current URL does not match Expected");

        Util.reportSauceLabsResult(driver, true);
        driver.close();
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

        Util.info(driver, ">>> The user should be shown a locked out message");
        Assert.assertTrue(loginPage.hasLockedOutError());

        Util.reportSauceLabsResult(driver, true);
        driver.close();
    }
}
