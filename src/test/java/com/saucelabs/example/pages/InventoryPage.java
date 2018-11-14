package com.saucelabs.example.pages;

import com.saucelabs.example.MyFluentWait;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.time.temporal.ChronoUnit;

public class InventoryPage extends AbstractPage
{
    public static final String PAGE_URL = "https://www.saucedemo.com/inventory.html";

    @FindBy(xpath = "//button[text()='Open Menu']")
    private WebElement hamburgerElem;

    @FindBy(css = "#shopping_cart_container > a")
    private WebElement shoppingCartElem;

    public InventoryPage(WebDriver driver)
    {
        super(driver);
        PageFactory.initElements(driver, this);

        wait = new MyFluentWait<WebDriver>(driver)
                .withTimeout(60, ChronoUnit.SECONDS)
                .pollingEvery(2, ChronoUnit.SECONDS)
                .ignoring(NoSuchElementException.class);
    }

    @Override
    public WebElement getPageLoadedTestElement()
    {
        return hamburgerElem;
    }

    public void addItemToCartByName(String itemName)
    {
        String xpath = String.format("//div[contains(., '%s')]/parent::a/parent::div/following-sibling::div/button", itemName);
        WebElement itemElem = getDriver().findElement(By.xpath(xpath));

        itemElem.click();
    }

    public void clickOnShoppingCart()
    {
        shoppingCartElem.click();
    }
}
