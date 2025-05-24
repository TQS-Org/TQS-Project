Feature: Role-based dashboard redirection after login

  Scenario Outline: Redirect user to correct dashboard based on role
    Given I am on the login page
    When I enter "<email>" and "<password>"
    And I click the login button
    Then I should be redirected to the "<dashboard>" page

    Examples:
      | email              | password    | dashboard |
      | driver@mail.com    | driverpass  | /driver   |
      | operator1@mail.com | operatorpass| /operator |
      | admin@mail.com     | adminpass   | /admin    |
