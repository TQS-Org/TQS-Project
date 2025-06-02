Feature: Book a charge

  Scenario: User maes a valid booking
    Given I am on the login page
    When I enter "driver@mail.com" and "driverpass"
    And I click the login button
    Then I should be redirected to the "/driver" page
    And I should see a list of EV charging stations
    When I click the first station in the list
    Then I should see a list of bookings for current day
    When I click Book button
    And I fill in the booking form with:
      | start     | 18-00        |
      | duration  | 30           |
    And click the confirm button:
    Then I should get an alert with the message "Booking Confirmed!"
    
    