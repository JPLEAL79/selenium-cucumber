Feature: Product Purchase Workflow

  Background: User is logged in
    Given the web application is available
    When the user logs in with username "USER_OK" and password "PASS_OK"
    And the user clicks the login button
    Then the products page should be displayed


  @purchase
  Scenario Outline: Successfully purchase a product
    Given the user adds the product "<product_name>" to the cart
    And the user navigates to the shopping cart
    And the user proceeds to checkout
    When the user enters the customer first name "<first_name>"
    And the user enters the customer last name "<last_name>"
    And the user enters the postal code "<postal_code>"
    And the user confirms the checkout information
    And the user confirms the product payment
    Then the purchase confirmation message should be displayed

    Examples:
      | product_name           | first_name          | last_name          | postal_code         |
      | PRODUCT_NAME_BACKPACK  | FIRST_NAME_DEFAULT  | LAST_NAME_DEFAULT  | POSTAL_CODE_DEFAULT |
