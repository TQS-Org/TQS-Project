package TQS.project.backend.steps;

import io.cucumber.java.Before;
import io.cucumber.java.en.*;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

public class SearchSteps {

  private WebDriver driver;
  private WebDriverWait wait;

  @Before
  public void setup() {
    WebDriverSingleton.initialize();
    driver = WebDriverSingleton.getDriver();
    wait = WebDriverSingleton.getWait();
  }

  @Then("I should see a list of EV charging stations")
  public void i_should_see_a_list_of_ev_charging_stations() {
    List<WebElement> cards = driver.findElements(By.className("station-card"));
    Assertions.assertFalse(
        cards.isEmpty(), "Expected to see a list of EV charging stations, but found none.");
  }

  @When("I enter {string} in the district filter")
  public void i_enter_district_in_the_district_filter(String district) {
    WebElement districtInput = driver.findElement(By.cssSelector("input[placeholder='District']"));
    districtInput.clear();
    districtInput.sendKeys(district);
  }

  @And("I click the {string} button")
  public void i_click_the_search_button(String buttonText) {
    WebElement searchButton =
        driver.findElements(By.tagName("button")).stream()
            .filter(btn -> btn.getText().equalsIgnoreCase(buttonText))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Search button not found"));
    searchButton.click();

    // Optionally wait for results to refresh
    try {
      Thread.sleep(1000); // Better: use WebDriverWait
    } catch (InterruptedException ignored) {
    }
  }

  @Then("I should see only stations located in {string}")
  public void i_should_see_only_stations_in_district(String expectedDistrict) {
    List<WebElement> cards = driver.findElements(By.className("station-card"));
    Assertions.assertFalse(cards.isEmpty(), "No stations found after search.");

    for (WebElement card : cards) {
      String text = card.getText().toLowerCase();
      Assertions.assertTrue(
          text.contains(expectedDistrict.toLowerCase()),
          "Card does not contain expected district: " + expectedDistrict);
    }
  }
}
