Feature: Update Station Information

  Scenario: Operator updates their assigned station information
    Given I am on the login page
    When I enter "operator1@mail.com" and "operatorpass"
    And I click the login button
    Then I should be redirected to the "/operator" page
    When I click the "Edit Station" button
    And I update the station name to "Updated Station Name"
    And I update the brand to "Updated Brand"
    And I update the address to "Updated Address, City"
    And I click the "Save" button
    Then I should see a message saying "Station updated successfully!"
    And I should see the updated details for the station
