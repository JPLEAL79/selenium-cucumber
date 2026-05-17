package pages;

import commons.Utils;
import definitions.Hooks;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;


public class LoginPage extends Utils {

    private final WebDriver driver;

    @FindBy(id = "user-name")
    private WebElement usernameInput;
    @FindBy(id = "password")
    private WebElement passwordInput;
    @FindBy(id = "login-button")
    private WebElement loginButton;
    @FindBy(xpath = "//h3[@data-test='error']")
    private WebElement errorMessageLabel;

    public LoginPage() {
        this.driver = Hooks.getDriver();
        PageFactory.initElements(this.driver, this);
    }


    // Abre la aplicación web.
    public void openApplication() {
        driver.get("https://www.saucedemo.com/v1/index.html");
    }

    /**
     * Ingresa el nombre de usuario.
     *
     * @param username usuario a ingresar
     */
    public void enterUsername(String username) {
        Utils.uiWait(driver).until(ExpectedConditions.visibilityOf(usernameInput));
        usernameInput.clear();
        usernameInput.sendKeys(username);
    }

    /**
     * Ingresa la contraseña.
     *
     * @param password contraseña a ingresar
     */
    public void enterPassword(String password) {
        Utils.uiWait(driver).until(ExpectedConditions.visibilityOf(passwordInput));
        passwordInput.clear();
        passwordInput.sendKeys(password);
    }

    // Hace click en el botón Login.
    public void clickLoginButton() {
        Utils.uiWait(driver).until(ExpectedConditions.elementToBeClickable(loginButton));
        loginButton.click();
    }

    /**
     * Valida que el mensaje de error visible sea el esperado.
     *
     * @param expectedMessage mensaje esperado desde el feature
     */
    public void validateErrorMessage(String expectedMessage) {
        Utils.uiWait(driver).until(ExpectedConditions.visibilityOf(errorMessageLabel));
        Assertions.assertEquals(
                expectedMessage,
                errorMessageLabel.getText(),
                "El mensaje de error mostrado no es el esperado"
        );
    }

    /**
     * Verifica si el mensaje de error está visible.
     * @return true si el mensaje de error está visible
     */
    public boolean isErrorMessageDisplayed() {
        return errorMessageLabel.isDisplayed();
    }
}
