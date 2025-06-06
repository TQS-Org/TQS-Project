package TQS.project.backend.steps;

import io.cucumber.java.Before;
import io.cucumber.java.en.*;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import java.time.Duration;

public class BookingSteps {

  private WebDriver driver;
  private WebDriverWait wait;

  @Before
  public void setup() {
    WebDriverSingleton.initialize();
    driver = WebDriverSingleton.getDriver();
    wait = WebDriverSingleton.getWait();
  }

  @Then("I should see a list of chargers")
  public void viewListOfChargers() {
    List<WebElement> chargers =
        wait.until(
            ExpectedConditions.visibilityOfAllElementsLocatedBy(
                By.cssSelector(".charger-list .charger-card")));
    Assertions.assertFalse(chargers.isEmpty(), "No chargers are visible");
  }

  @When("I click the first charger in the list")
  public void clickTheFirstCharger() {
    List<WebElement> chargerCards =
        wait.until(
            ExpectedConditions.visibilityOfAllElementsLocatedBy(
                By.cssSelector(".charger-list .charger-card")));
    Assertions.assertFalse(chargerCards.isEmpty(), "No charger cards found");
    chargerCards.get(0).click();
  }

  @Then("I should see the status value as {string}")
  public void viewBookingsForToday(String statusValue) {
    WebElement statusBadge =
        new WebDriverWait(driver, Duration.ofSeconds(5))
            .until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".status-badge")));

    Assertions.assertEquals(
        statusValue, statusBadge.getText(), "Status text does not match expected value");
  }

  @When("I select the next day")
  public void selectNextDay() {
    WebElement dateInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("date")));
    LocalDate tomorrow = LocalDate.now().plusDays(1);
    String nextDayStr = tomorrow.format(DateTimeFormatter.ISO_DATE);
    ((JavascriptExecutor) driver)
        .executeScript(
            "arguments[0].value = arguments[1];"
                + "arguments[0].dispatchEvent(new Event('input', { bubbles: true }));"
                + "arguments[0].dispatchEvent(new Event('change', { bubbles: true }));",
            dateInput,
            nextDayStr);
  }

  // @And("I click the {string} button")
  // public void clickButtonByText(String buttonText) {
  // WebElement button = wait.until(ExpectedConditions.elementToBeClickable(
  // By.xpath("//button[contains(normalize-space(), '" + buttonText + "')]")));
  // button.click();
  // }

  @And("I fill in the booking form with:")
  public void fillBookingForm(io.cucumber.datatable.DataTable dataTable) {
    List<List<String>> data = dataTable.asLists(String.class);
    for (List<String> row : data) {
      String field = row.get(0).toLowerCase();
      String value = row.get(1);
      if (field.contains("starttime")) {
        WebElement select =
            wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("form#booking-form select")));
        select.click();
        WebElement option = select.findElement(By.cssSelector("option[value='" + value + "']"));
        option.click();
      } else if (field.contains("duration")) {
        WebElement input =
            wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                    By.cssSelector("form#booking-form input[type='number']")));
        input.clear();
        input.sendKeys(value);
      }
    }
  }

  @And("click the form {string} button")
  public void confirmBookingButton(String buttonText) {
    WebElement confirmBtn =
        wait.until(
            ExpectedConditions.elementToBeClickable(
                By.xpath("//form[@id='booking-form']//button[contains(., '" + buttonText + "')]")));
    confirmBtn.click();
  }

  @Then("I should get an alert with the message {string}")
  public void verifySuccessMessage(String expectedMessage) {
    WebElement successMessage =
        wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".feedback.success")));
    Assertions.assertEquals(expectedMessage, successMessage.getText().trim());
  }
}
