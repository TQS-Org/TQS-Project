Feature: Create a Station Operator account

  Scenario: Admin creates a new operator account successfully
    Given I am on the login page
    When I enter "admin@mail.com" and "adminpass"
    And I click the login button
    Then I should be redirected to the "/admin" page
    And I should see a list of operators
    When I click on Create Operator
    And I fill in the registration form with:
      | name             | Bob                 |
      | email            | bobbob@example.com  |
      | password         | bobpass123          |
      | confirm password | bobpass123          |
      | age              | 23                  |
      | number           | 912345678           |
      | address          | Rua fixe            |
    And I click the "Create" button
    Then I should see a success modal with the message "Operator Created!"
