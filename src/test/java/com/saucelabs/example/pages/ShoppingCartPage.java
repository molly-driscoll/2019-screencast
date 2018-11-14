package com.saucelabs.example.pages;

import com.saucelabs.example.MyFluentWait;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.time.temporal.ChronoUnit;
import java.util.List;

public class ShoppingCartPage extends AbstractPage
{
    public static final String PAGE_URL = "https://www.saucedemo.com/inventory.html";

    @FindBy(xpath = "//button[text()='Open Menu']")
    private WebElement hamburgerElem;

    @FindBy(css = "#shopping_cart_container > a")
    private WebElement shoppingCartElem;

    @FindAll({@FindBy(css = "#cart_contents_container > div > div.cart_list > div.cart_item")})
    private List<WebElement> itemsList;

    public ShoppingCartPage(WebDriver driver)
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

    public int getItemCount()
    {
        return itemsList.size();
    }

}
