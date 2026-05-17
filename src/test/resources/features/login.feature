Feature: Web Login Functionality

  @login @positive
  Scenario Outline: Successful login
    Given the web application is available
    When the user logs in with username "<username>" and password "<password>"
    And the user clicks the login button
    Then the products page should be displayed

    Examples:
      | username | password |
      | USER_OK  | PASS_OK  |


  @login @negative
  Scenario Outline: Unsuccessful login
    Given the web application is available
    When the user logs in with username "<username>" and password "<password>"
    And the user clicks the login button
    Then an error message "<error_message>" should be displayed

    Examples:
      | username      | password     | error_message                                       |
      | USER_LOCKED   | PASS_OK      | Epic sadface: Sorry, this user has been locked out. |
      |               |              | Epic sadface: Username is required                  |
