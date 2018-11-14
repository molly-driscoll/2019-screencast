package com.saucelabs.example.stepdefs;

import com.saucelabs.example.pages.PagesFactory;
import com.saucelabs.example.pages.ShoppingCartPage;
import cucumber.api.java8.En;
import org.testng.Assert;

public class ShoppingCartSteps implements En
{
    public ShoppingCartSteps()
    {
        Then("^There should be \"([^\"]*)\" items in the shopping cart$", (String count) -> {

            ShoppingCartPage shoppingCartPage = PagesFactory.getInstance().getShoppingCartPage();
            int actualCount = shoppingCartPage.getItemCount();
            int expectedCount = Integer.parseInt(count);

            Assert.assertEquals(actualCount, expectedCount);
        });
    }
}
