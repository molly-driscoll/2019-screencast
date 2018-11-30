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
    }
}
