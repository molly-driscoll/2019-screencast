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
    private static String username = System.getenv("SAUCE_USERNAME");
    private static String accessKey = System.getenv("SAUCE_ACCESS_KEY");

    @Test
    public void verifyValidUsersCanSignIn()
    throws MalformedURLException
    {
        MutableCapabilities caps = MutableCapabilities();
	    caps.setCapability("browserName", "chrome");
        caps.setCapability("platform", "Windows 10");
        caps.setCapability("version", "73.0");

        caps.setCapability("username", username);
        caps.setCapability("accesskey", accessKey);
        caps.setCapability("name", "Verify Valid Users Can Sign In");

        RemoteWebDriver driver = new RemoteWebDriver(new URL("https://ondemand.saucelabs.com:443/wd/hub"), caps);

        LoginPage loginPage = new LoginPage(driver);
        InventoryPage inventoryPage = new InventoryPage(driver);

        loginPage.navigateTo(LoginPage.PAGE_URL);
        loginPage.enterUsername("standard_user");
        loginPage.enterPassword("secret_sauce");

        loginPage.clickLogin();
        inventoryPage.waitForPageLoad();

        String currentUrl = driver.getCurrentUrl();
        Assert.assertEquals(currentUrl, InventoryPage.PAGE_URL, "Current URL does not match Expected");

        driver.quit();
    }
}
