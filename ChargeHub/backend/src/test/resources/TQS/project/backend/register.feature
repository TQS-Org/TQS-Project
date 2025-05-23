Feature: User Registration

  Scenario: Successful registration with valid details
    Given I am on the login page
    When I click on "CreateAccount"
    And I fill in the registration form with:
      | name     | Bob           |
      | age      | 30            |
      | email    | bob@example.com |
      | number   | 919191919     |
      | password | bobpass123    |
    And I click the Sign Up button
    Then I should be redirected to the "/driver" page
