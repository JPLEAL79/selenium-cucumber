package definitions;

import data.LoginDataResolver;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import pages.LoginPage;
import pages.ProductsPage;


public class LoginDefinition {

    private LoginPage loginPage() {
        return new LoginPage();
    }

    private ProductsPage productsPage() {
        return new ProductsPage();
    }

    @Given("the web application is available")
    public void theWebApplicationIsAvailable() {
        loginPage().openApplication();
    }

    /**
     * Login usando keys lógicas definidas en el feature.
     * Las keys se resuelven a valores reales mediante LoginDataResolver.
     * Aplica tanto para escenarios positivos como negativos.
     */
    @When("the user logs in with username {string} and password {string}")
    public void theUserLogsInWithUsernameAndPassword(String usernameKey, String passwordKey) {

        // Resuelve la key lógica a su valor real (o vacío si aplica)
        String username = LoginDataResolver.resolve(usernameKey);
        String password = LoginDataResolver.resolve(passwordKey);
        loginPage().enterUsername(username);
        loginPage().enterPassword(password);
    }

    @And("the user clicks the login button")
    public void theUserClicksTheLoginButton() {
        loginPage().clickLoginButton();
    }

    @Then("the products page should be displayed")
    public void theProductsPageShouldBeDisplayed() {
        Assertions.assertTrue(
                productsPage().isProductsPageDisplayed(),
                "Products page was not displayed after login"
        );
    }

     //Valida el mensaje de error mostrado en la UI.
    @Then("an error message {string} should be displayed")
    public void anErrorMessageShouldBeDisplayed(String errorMessage) {
        loginPage().validateErrorMessage(errorMessage);
    }
}
