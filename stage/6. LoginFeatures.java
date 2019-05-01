package com.saucelabs.example.features;

import com.saucelabs.example.pages.InventoryPage;
import com.saucelabs.example.pages.LoginPage;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;

public class LoginFeatures extends BaseFeature
{
    private static String username = System.getenv("SAUCE_USERNAME");
    private static String accessKey = System.getenv("SAUCE_ACCESS_KEY");

    @Test(dataProvider = "desktopDataProvider")
    public void verifyValidUsersCanSignIn(String browser, String browserVersion, String platform, Method method)
    throws MalformedURLException
    {
        URL url = new URL("https://ondemand.saucelabs.com:443/wd/hub");

        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("browserName", browser);
        caps.setCapability("version", browserVersion);
        caps.setCapability("platform", platform);
        caps.setCapability("avoidProxy", true);

        caps.setCapability("username", username);
        caps.setCapability("accessKey", accessKey);
        caps.setCapability("name", "Verify Valid Users Can Sign In");
        caps.setCapability("build", "build-1234");

        caps.setCapability("extendedDebugging", true);
        caps.setCapability("capturePerformance", true);

        RemoteWebDriver driver = new RemoteWebDriver(url, caps);

        LoginPage loginPage = new LoginPage(driver);
        InventoryPage inventoryPage = new InventoryPage(driver);

        JavascriptExecutor jsExec = (JavascriptExecutor) driver;
        jsExec.executeScript("sauce:context=>>> Verify we are on the Inventory Page");

        loginPage.navigateTo(LoginPage.PAGE_URL);
        loginPage.enterUsername("standard_user");
        loginPage.enterPassword("secret_sauce");

        loginPage.clickLogin();
        inventoryPage.waitForPageLoad();

        String currentUrl = driver.getCurrentUrl();
        Assert.assertEquals(currentUrl, InventoryPage.PAGE_URL, "Current URL does not match Expected");

        jsExec.executeScript("sauce:job-result=true");
        driver.quit();
    }
}
