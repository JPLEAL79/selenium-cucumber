package pages;

import commons.Utils;
import definitions.Hooks;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class ProductsPage extends Utils {

    private final WebDriver driver;

    /* ========= Locators fijos ========= */

    @FindBy(xpath = "//*[@id='header_container']/div[2]")
    private WebElement productsPageTitle;

    @FindBy(id = "add-to-cart-sauce-labs-backpack")
    private WebElement addBackpackButton;

    @FindBy(id = "shopping_cart_container")
    private WebElement cartButton;

    @FindBy(xpath = "//div[@class='inventory_item_name' and text()='Sauce Labs Backpack']")
    private WebElement backpackInCart;

    public ProductsPage() {
        this.driver = Hooks.getDriver();
        PageFactory.initElements(this.driver, this);
    }

    // Verifica si la página de productos está visible.
    public boolean isProductsPageDisplayed() {
        Utils.uiWait(driver)
                .until(ExpectedConditions.visibilityOf(productsPageTitle));
        return productsPageTitle.isDisplayed();
    }

    // Verifica si el producto Backpack está en el carrito.
    public boolean isBackpackInCart() {
        return backpackInCart.isDisplayed();
    }

    /* ========= Acciones ========= */

    // Agrega el producto Backpack al carrito.
    public void addBackpackToCart() {
        Utils.uiWait(driver)
                .until(ExpectedConditions.elementToBeClickable(addBackpackButton));
        addBackpackButton.click();
    }

    // Navega al carrito de compras.
    public void goToCart() {
        Utils.uiWait(driver)
                .until(ExpectedConditions.elementToBeClickable(cartButton));
        cartButton.click();
    }
}
