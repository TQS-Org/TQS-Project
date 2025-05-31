Feature: View Route for EV Charging Stations

  Scenario: User views the route to a charging station
    Given I am on the login page
    When I enter "driver@mail.com" and "driverpass"
    And I click the login button
    Then I should be redirected to the "/driver" page
    And I should see a list of EV charging stations
    When I click the first station in the list
    Then I should be redirected to the "/stations/1" page
    And I should see a map displaying the route from my location to the station