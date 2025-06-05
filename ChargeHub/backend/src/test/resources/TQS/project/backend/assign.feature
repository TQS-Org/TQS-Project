Feature: Admin can assign a station to an operator

  Scenario: Add a station, add a charger, and assign to an operator
    Given I am on the login page
    When I enter "admin@mail.com" and "adminpass"
    And I click the login button
    Then I should be redirected to the "/admin" page
    And I should see a list of operators
    When I navigate to the admin stations page
    And I click on Create Station

    And I create a new station with valid details
    And I open the newly created station and add a charger
    And I navigate to the admin operators page
    And I assign the station to an operator
    Then I should see a success message
