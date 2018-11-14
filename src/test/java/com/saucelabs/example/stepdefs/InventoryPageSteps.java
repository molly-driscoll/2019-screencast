package com.saucelabs.example.stepdefs;

import com.saucelabs.example.pages.InventoryPage;
import com.saucelabs.example.pages.PagesFactory;
import cucumber.api.java8.En;
import io.cucumber.datatable.DataTable;

import java.util.List;

public class InventoryPageSteps implements En
{
    public InventoryPageSteps()
    {
        And("^The user chooses a \"([^\"]*)\" by clicking 'Add To Cart'$", (String itemName) -> {
            InventoryPage inventoryPage = PagesFactory.getInstance().getInventoryPage();
            inventoryPage.addItemToCartByName(itemName);
        });

        And("^The user clicks on the shopping cart$", () -> {
            InventoryPage inventoryPage = PagesFactory.getInstance().getInventoryPage();
            inventoryPage.clickOnShoppingCart();
        });

        And("^The user selects$", (DataTable dataTable) -> {
            InventoryPage inventoryPage = PagesFactory.getInstance().getInventoryPage();

            List<String> selectedItems = dataTable.asList(String.class);
            for (String itemName : selectedItems)
            {
                System.out.println(itemName);
                inventoryPage.addItemToCartByName(itemName);
            }
        });

//        Given("^The user is on the Home Page$", () -> {
//            LoginPage loginPage = PagesFactory.getInstance().getLoginPage();
//            loginPage.navigateTo(LoginPage.PAGE_URL);
//
//            Util.getSaucePerformance(PagesFactory.getInstance().getDriver());
//        });
//
//        And("^The user provides the username as \"([^\"]*)\" and password as \"([^\"]*)\"$",
//                (String username, String password) -> {
//            LoginPage loginPage = PagesFactory.getInstance().getLoginPage();
//            loginPage.enterUsername(username);
//            loginPage.enterPassword(password);
//        });
//
//        And("^The user clicks the 'Login' button$", () -> {
//            LoginPage loginPage = PagesFactory.getInstance().getLoginPage();
//            loginPage.clickLogin();
//        });
//
//        Then("^The user should login successfully and is brought to the inventory page$", () -> {
//            InventoryPage inventoryPage = PagesFactory.getInstance().getInventoryPage();
//
////            inventoryPage.waitForPageLoad();
//            String currentUrl = PagesFactory.getInstance().getDriver().getCurrentUrl();
//            Assert.assertEquals(currentUrl, inventoryPage.PAGE_URL);
//        });
//
//        Then("^The user should be shown a locked out message$", () -> {
//            LoginPage loginPage = PagesFactory.getInstance().getLoginPage();
//
//            Assert.assertTrue(loginPage.hasLockedOutError());
//        });
//
//        Then("^The user should be shown an invalid username/password message$", () -> {
//            LoginPage loginPage = PagesFactory.getInstance().getLoginPage();
//
//            Assert.assertTrue(loginPage.hasUsernamePasswordError());
//        });
    }
}
