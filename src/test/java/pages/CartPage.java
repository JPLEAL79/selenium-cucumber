package pages;

import commons.Utils;
import definitions.Hooks;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class CartPage extends Utils {

    private final WebDriver driver;

    /* ========= Locators fijos ========= */

    @FindBy(id = "checkout")
    private WebElement checkoutButton;

    public CartPage() {
        this.driver = Hooks.getDriver();
        PageFactory.initElements(this.driver, this);
    }

    /* ========= Acciones ========= */

    /**
     * Avanza al proceso de checkout.
     */
    public void clickCheckout() {
        Utils.uiWait(driver).until(ExpectedConditions.elementToBeClickable(checkoutButton));
        checkoutButton.click();
    }
}
