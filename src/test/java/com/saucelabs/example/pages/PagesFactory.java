package com.saucelabs.example.pages;

import org.openqa.selenium.WebDriver;

public class PagesFactory
{
    private static PagesFactory instance;
    private WebDriver driver;
    private LoginPage loginPage;
    private InventoryPage inventoryPage;
    private ShoppingCartPage shoppingCartPage;

    public static void start(WebDriver driver)
    {
        instance = new PagesFactory(driver);
    }

    public static PagesFactory getInstance()
    {
        return instance;
    }

    private PagesFactory(WebDriver driver)
    {
        this.driver = driver;
        loginPage = new LoginPage(driver);
        inventoryPage = new InventoryPage(driver);
        shoppingCartPage = new ShoppingCartPage(driver);
    }

    public WebDriver getDriver()
    {
        return driver;
    }

    public LoginPage getLoginPage()
    {
        return loginPage;
    }

    public InventoryPage getInventoryPage()
    {
        return inventoryPage;
    }

    public ShoppingCartPage getShoppingCartPage() { return shoppingCartPage; }
}
