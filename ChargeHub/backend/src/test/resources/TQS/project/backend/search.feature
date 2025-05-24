Feature: Search EV Charging Stations via UI

  Scenario: User filters by district
    Given I am on the login page
    When I enter "driver@mail.com" and "driverpass"
    And I click the login button
    Then I should be redirected to the "/driver" page
    And I should see a list of EV charging stations
    When I enter "Lisboa" in the district filter
    And I click the "Search" button
    Then I should see only stations located in "Lisboa"
