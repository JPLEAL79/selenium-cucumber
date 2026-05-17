package pages;

import commons.Utils;
import definitions.Hooks;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class CheckoutPage extends Utils {

    private final WebDriver driver;

    /* ========= Locators fijos ========= */

    @FindBy(id = "first-name")
    private WebElement firstNameInput;
    @FindBy(id = "last-name")
    private WebElement lastNameInput;
    @FindBy(id = "postal-code")
    private WebElement postalCodeInput;
    @FindBy(id = "continue")
    private WebElement continueButton;
    @FindBy(id = "finish")
    private WebElement finishButton;
    @FindBy(className = "complete-header")
    private WebElement purchaseConfirmationMessage;

    public CheckoutPage() {
        this.driver = Hooks.getDriver();
        PageFactory.initElements(this.driver, this);
    }

    /* ========= Acciones ========= */

    // Ingresa el nombre del cliente.
    public void enterFirstName(String firstName) {
        Utils.uiWait(driver).until(ExpectedConditions.visibilityOf(firstNameInput));
        firstNameInput.sendKeys(firstName);
    }

    // Ingresa el apellido del cliente.
    public void enterLastName(String lastName) {
        Utils.uiWait(driver).until(ExpectedConditions.visibilityOf(lastNameInput));
        lastNameInput.sendKeys(lastName);
    }


    // Ingresa el código postal del cliente.
    public void enterPostalCode(String postalCode) {
        Utils.uiWait(driver).until(ExpectedConditions.visibilityOf(postalCodeInput));
        postalCodeInput.sendKeys(postalCode);
    }


    // Continúa al resumen del checkout.
    public void continueCheckout() {
        Utils.uiWait(driver).until(ExpectedConditions.elementToBeClickable(continueButton));
        continueButton.click();
    }

    // Confirma la compra del producto.
    public void finishCheckout() {
        Utils.uiWait(driver).until(ExpectedConditions.elementToBeClickable(finishButton));
        finishButton.click();
    }

    /* ========= Validaciones simples ========= */


    // Verifica si el mensaje de confirmación de compra está visible.
    public boolean isPurchaseConfirmationDisplayed() {
        return purchaseConfirmationMessage.isDisplayed();
    }
}
