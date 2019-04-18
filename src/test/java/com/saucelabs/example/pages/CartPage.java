package com.saucelabs.example.pages;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.FindAll;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import java.util.List;

public class CartPage extends AbstractPage
{
    public static final String PAGE_URL = "https://www.saucedemo.com/cart.html";

    @FindBy(xpath = "//button[text()='Open Menu']")
    private WebElement hamburgerElem;

    @FindBy(css = "a.btn_action.checkout_button")
    private WebElement checkoutElem;

    @FindBy(css = "#shopping_cart_container > a")
    private WebElement shoppingCartElem;

    @FindAll({@FindBy(css = "#cart_contents_container > div > div.cart_list > div.cart_item")})
    private List<WebElement> itemsList;

    public CartPage(RemoteWebDriver driver)
    {
        super(driver);
        PageFactory.initElements(driver, this);
    }

    @Override
    public WebElement getPageLoadedTestElement()
    {
        return hamburgerElem;
    }

    public void clickCheckout()
    {
        checkoutElem.click();
    }

    public int getItemCount()
    {
        return itemsList.size();
    }

}
