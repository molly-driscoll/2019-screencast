package com.saucelabs.example.pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

public class CheckOutStepTwoPage extends AbstractPage
{
    public static final String PAGE_URL = "https://www.saucedemo.com/checkout-step-two.html";

    @FindBy(css = "div.summary_subtotal_label")
    private WebElement itemTotalElem;

    @FindBy(css = "div.summary_tax_label")
    private WebElement taxElem;

    @FindBy(css = "div.summary_total_label")
    private WebElement totalElem;

    public CheckOutStepTwoPage(RemoteWebDriver driver)
    {
        super(driver);
        PageFactory.initElements(driver, this);
    }

    @Override
    public WebElement getPageLoadedTestElement()
    {
        return itemTotalElem;
    }

    public String getItemTotal()
    {
        return itemTotalElem.getText();
    }

    public String getTax()
    {
        return taxElem.getText();
    }

    public String getTotal()
    {
        return totalElem.getText();
    }
}


