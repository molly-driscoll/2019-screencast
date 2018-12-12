package com.saucelabs.example.stepdefs;

import com.saucelabs.example.Util;
import com.saucelabs.example.pages.InventoryPage;
import com.saucelabs.example.pages.LoginPage;
import com.saucelabs.example.pages.PagesFactory;
import cucumber.api.java8.En;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

public class LoginPageSteps implements En
{
    public LoginPageSteps()
    {
        Given("^The user is on the Home Page$", () -> {
            LoginPage loginPage = PagesFactory.getInstance().getLoginPage();
            loginPage.navigateTo(LoginPage.PAGE_URL);
        });

        And("^The user provides the username as \"([^\"]*)\" and password as \"([^\"]*)\"$",
                (String username, String password) -> {
            LoginPage loginPage = PagesFactory.getInstance().getLoginPage();
            loginPage.enterUsername(username);
            loginPage.enterPassword(password);
            Util.sleep(1000);
        });

        And("^The user clicks the 'Login' button$", () -> {
            LoginPage loginPage = PagesFactory.getInstance().getLoginPage();
            loginPage.clickLogin();
        });

        Then("^The user should login successfully and is brought to the inventory page$", () -> {
            PagesFactory pf = PagesFactory.getInstance();
            WebDriver driver = pf.getDriver();

            InventoryPage inventoryPage = pf.getInventoryPage();

            String currentUrl = PagesFactory.getInstance().getDriver().getCurrentUrl();
            Assert.assertEquals(currentUrl, inventoryPage.PAGE_URL);

            Util.info(driver, "The user should login successfully and is brought to the inventory page");
            Util.getSaucePerformance(PagesFactory.getInstance().getDriver());
//            Util.takeScreenShot(driver);
        });

        Then("^The user should be shown a locked out message$", () -> {
            LoginPage loginPage = PagesFactory.getInstance().getLoginPage();

            Assert.assertTrue(loginPage.hasLockedOutError());
        });

        Then("^The user should be shown an invalid username/password message$", () -> {
            LoginPage loginPage = PagesFactory.getInstance().getLoginPage();

            Assert.assertTrue(loginPage.hasUsernamePasswordError());
        });
    }
}
