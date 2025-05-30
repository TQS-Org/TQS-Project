package TQS.project.backend.steps;

import io.cucumber.java.Before;
import io.cucumber.java.en.*;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;

public class MapViewSteps {

  private WebDriver driver;
  private WebDriverWait wait;

  @Before
  public void setup() {
    WebDriverSingleton.initialize();
    driver = WebDriverSingleton.getDriver();
    wait = WebDriverSingleton.getWait();
  }

  @Then("I should see a map with only stations located in {string}")
  public void i_should_see_a_map_with_filtered_stations(String expectedDistrict) {
    WebElement map = driver.findElement(By.id("station-map"));
    Assertions.assertTrue(map.isDisplayed(), "Map is not visible.");
  }
}
