package com.saucelabs.example.stepdefs;

import com.saucelabs.example.Util;
import com.saucelabs.example.pages.CheckOutStepOnePage;
import com.saucelabs.example.pages.CheckOutStepTwoPage;
import com.saucelabs.example.pages.PagesFactory;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Then;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

public class CheckoutSteps
{
    @And("^The user provides the first name as \"([^\"]*)\" and last name as \"([^\"]*)\" and zip code as \"([^\"]*)\"$")
    public void theUserProvidesTheFirstNameAsAndLastNameAsAndZipCodeAs(String firstName, String lastName, String zipCode)
    throws Throwable
    {
        PagesFactory pf = PagesFactory.getInstance();
        WebDriver driver = pf.getDriver();

        Util.info(driver, ">>> The user provides the first and last name and password");

        CheckOutStepOnePage page = pf.getCheckOutStepOnePage();
        page.enterFirstName(firstName);
        page.enterLastName(lastName);
        page.enterPostalCode(zipCode);
    }

    @And("^The user clicks 'Continue'$")
    public void theUserClicksContinue()
    throws Throwable
    {
        PagesFactory pf = PagesFactory.getInstance();
        WebDriver driver = pf.getDriver();

        Util.info(driver, ">>> The user clicks 'Continue'");

        CheckOutStepOnePage page = pf.getCheckOutStepOnePage();
        page.clickContinue();
    }

    @Then("^The item total should be \"([^\"]*)\"$")
    public void theItemTotalShouldBe(String itemTotal)
    throws Throwable
    {
        PagesFactory pf = PagesFactory.getInstance();
        WebDriver driver = pf.getDriver();

        CheckOutStepTwoPage page = pf.getCheckOutStepTwoPage();

        String expected = itemTotal;
        String actual = page.getItemTotal();

        Assert.assertEquals(actual, expected);

        Util.info(driver,">>> The item total should be '%s'", expected);
        Util.getSaucePerformance(driver);
        Util.takeScreenShot(driver);
    }

    @And("^The tax should be \"([^\"]*)\"$")
    public void theTaxShouldBe(String tax)
    throws Throwable
    {
        PagesFactory pf = PagesFactory.getInstance();
        WebDriver driver = pf.getDriver();

        CheckOutStepTwoPage page = pf.getCheckOutStepTwoPage();

        String expected = tax;
        String actual = page.getTax();

        Assert.assertEquals(actual, expected);
        Util.info(driver, ">>> The tax should be '%s'", expected);
    }

    @And("^The total should be \"([^\"]*)\"$")
    public void theTotalShouldBe(String total)
    throws Throwable
    {
        PagesFactory pf = PagesFactory.getInstance();
        WebDriver driver = pf.getDriver();

        CheckOutStepTwoPage page = pf.getCheckOutStepTwoPage();

        String expected = total;
        String actual = page.getTotal();

        Assert.assertEquals(actual, expected);
        Util.info(driver, ">>> The total should be '%s'", expected);
    }
}
