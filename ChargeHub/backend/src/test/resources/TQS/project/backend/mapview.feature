Feature: View Stations on a Map

  Scenario: User views Stations on a Map
    Given I am on the login page
    When I enter "driver@mail.com" and "driverpass"
    And I click the login button
    Then I should be redirected to the "/driver" page
    And I should see a list of EV charging stations
    When I enter "Lisboa" in the district filter
    And I click the "Map View" button
    Then I should see a map with only stations located in "Lisboa"
