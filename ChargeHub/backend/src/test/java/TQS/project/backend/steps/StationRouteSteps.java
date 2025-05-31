package TQS.project.backend.steps;

import io.cucumber.java.Before;
import io.cucumber.java.en.*;
import org.junit.jupiter.api.Assertions;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.List;

public class StationRouteSteps {

  private WebDriver driver;
  private WebDriverWait wait;

  @Before
  public void setup() {
    WebDriverSingleton.initialize();
    driver = WebDriverSingleton.getDriver();
    wait = WebDriverSingleton.getWait();
  }

  @When("I click the first station in the list")
  public void clickFirstStationInList() {
    List<WebElement> stations = driver.findElements(By.cssSelector(".station-list .station-card"));
    if (!stations.isEmpty()) {
      stations.get(0).click();
    } else {
      Assertions.fail("No stations found in the list");
    }
  }

  @And("I should see a map displaying the route from my location to the station")
  public void verifyMapRoute() {
    // Wait until the station-details-map is loaded and visible
    WebElement map =
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("station-details-map")));
    Assertions.assertTrue(map.isDisplayed(), "Map is not displayed on the station details page");
  }
}
