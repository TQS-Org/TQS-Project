package TQS.project.backend.steps;

import io.cucumber.java.Before;
import io.cucumber.java.en.*;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;

public class NearbyStationsSteps {

  private WebDriver driver;
  private WebDriverWait wait;

  @Before
  public void setup() {
    WebDriverSingleton.initialize();
    driver = WebDriverSingleton.getDriver();
    wait = WebDriverSingleton.getWait();
  }

  @Then("I should see a different list of EV charging stations")
  public void then_i_should_see_a_different_list_of_ev_charging_stations() {
    List<WebElement> cards = driver.findElements(By.className("station-card"));
    Assertions.assertFalse(cards.isEmpty(), "No stations found after sorting by distance.");
  }
}
