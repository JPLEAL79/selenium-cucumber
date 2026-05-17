package definitions;

import data.PurchaseDataResolver;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import pages.CartPage;
import pages.CheckoutPage;
import pages.ProductsPage;


public class ProductPurchasesDefinition {

    private ProductsPage productsPage() {
        return new ProductsPage();
    }

    private CartPage cartPage() {
        return new CartPage();
    }

    private CheckoutPage checkoutPage() {
        return new CheckoutPage();
    }

    /* ========= Actions ========= */

    @Given("the user adds the product {string} to the cart")
    public void theUserAddsTheProductToTheCart(String productKey) {

        // Se resuelve la key para validar que el dato exista aunque
        // por ahora el flujo siga cubriendo el producto demo fijo.
        PurchaseDataResolver.resolve(productKey);
        productsPage().addBackpackToCart();
    }

    @And("the user navigates to the shopping cart")
    public void theUserNavigatesToTheShoppingCart() {
        productsPage().goToCart();
    }

    @And("the user proceeds to checkout")
    public void theUserProceedsToCheckout() {
        cartPage().clickCheckout();
    }

    @When("the user enters the customer first name {string}")
    public void theUserEntersTheCustomerFirstName(String firstNameKey) {

        // Resuelve la key lógica
        String firstName = PurchaseDataResolver.resolve(firstNameKey);
        checkoutPage().enterFirstName(firstName);
    }

    @And("the user enters the customer last name {string}")
    public void theUserEntersTheCustomerLastName(String lastNameKey) {

        // Resuelve la key lógica
        String lastName = PurchaseDataResolver.resolve(lastNameKey);
        checkoutPage().enterLastName(lastName);
    }

    @And("the user enters the postal code {string}")
    public void theUserEntersThePostalCode(String postalCodeKey) {

        // Resuelve la key lógica
        String postalCode = PurchaseDataResolver.resolve(postalCodeKey);
        checkoutPage().enterPostalCode(postalCode);
    }

    @And("the user confirms the checkout information")
    public void theUserConfirmsTheCheckoutInformation() {
        checkoutPage().continueCheckout();
    }

    @And("the user confirms the product payment")
    public void theUserConfirmsTheProductPayment() {
        checkoutPage().finishCheckout();
    }

    /* ========= Validations ========= */

    @Then("the purchase confirmation message should be displayed")
    public void thePurchaseConfirmationMessageShouldBeDisplayed() {
        Assertions.assertTrue(
                checkoutPage().isPurchaseConfirmationDisplayed(),
                "Purchase confirmation message should be displayed"
        );
    }
}
