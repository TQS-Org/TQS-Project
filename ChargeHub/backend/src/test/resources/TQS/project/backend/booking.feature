Feature: Book a charge

  Scenario: User makes a valid booking
    Given I am on the login page
    When I enter "driver@mail.com" and "driverpass"
    And I click the login button
    Then I should be redirected to the "/driver" page
    And I should see a list of EV charging stations
    When I click the first station in the list
    Then I should see a list of chargers
    When I click the first charger in the list
    Then I should see the status value as "Available"
    #When I select the next day
    #And I click the "Book Charge" button
    #And I fill in the booking form with:
    #  | startTime     | 23:40        |
    #  | duration      | 10           |
    #And click the form "Confirm Booking" button
    #Then I should get an alert with the message "Booking created successfully!"
    
    