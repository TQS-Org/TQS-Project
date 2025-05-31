Feature: Search Nearby EV Charging Stations

  Scenario: User sorts by distance
    Given I am on the login page
    When I enter "driver@mail.com" and "driverpass"
    And I click the login button
    Then I should be redirected to the "/driver" page
    And I should see a list of EV charging stations
    When I click the "See Nearby Stations" button
    Then I should see a different list of EV charging stations
