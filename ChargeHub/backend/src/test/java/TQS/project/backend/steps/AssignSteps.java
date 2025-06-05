package TQS.project.backend.steps;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.openqa.selenium.support.ui.Select;

import java.time.Duration;
import java.util.List;

import io.cucumber.java.Before;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class AssignSteps {

  private WebDriver driver;
  private WebDriverWait wait;

  @Before
  public void setup() {
    WebDriverSingleton.initialize();
    driver = WebDriverSingleton.getDriver();
    wait = WebDriverSingleton.getWait();
  }

  @When("I navigate to the admin stations page")
  public void iNavigateToTheAdminStationsPage() {
    driver.findElement(By.xpath("//li[text()='Show Stations']")).click();
  }

  @When("I click on Create Station")
  public void i_click_on_create_operator() {
    WebElement button =
        driver.findElements(By.className("create-card")).stream()
            .filter(el -> el.getText().contains("Create Station"))
            .findFirst()
            .orElseThrow();
    button.click();
  }

  @When("I create a new station with valid details")
  public void iCreateANewStationWithValidDetails() {
    driver.findElement(By.name("name")).sendKeys("Test Station");
    driver.findElement(By.name("brand")).sendKeys("BrandX");
    driver.findElement(By.name("latitude")).sendKeys("40.123456");
    driver.findElement(By.name("longitude")).sendKeys("-8.123456");
    driver.findElement(By.name("address")).sendKeys("123 Test St");
    driver.findElement(By.name("numberOfChargers")).sendKeys("1");
    driver.findElement(By.name("openingHours")).sendKeys("08:00");
    driver.findElement(By.name("closingHours")).sendKeys("22:00");
    driver.findElement(By.name("price")).sendKeys("0.20");

    // Submit the form
    driver.findElement(By.cssSelector("form button[type='submit']")).click();

    // Wait for and click the "Close" button on the success modal
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    WebElement closeButton =
        wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".modal.success-animation .close-button")));
    closeButton.click();

    // Wait until the modal disappears
    wait.until(
        ExpectedConditions.invisibilityOfElementLocated(
            By.cssSelector(".modal.success-animation")));
  }

  @When("I open the newly created station and add a charger")
  public void iOpenTheNewlyCreatedStationAndAddACharger() {
    // Wait for station list to update
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
    wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".operator-card")));

    // Click on the new station (assuming it’s the first or has a unique name)
    driver.findElements(By.cssSelector(".operator-card")).get(1).click();

    // Fill in charger form in modal
    driver.findElement(By.name("type")).sendKeys("AC");
    driver.findElement(By.name("connectorType")).sendKeys("Type 2");
    driver.findElement(By.name("power")).sendKeys("22");
    driver.findElement(By.cssSelector("form button[type='submit']")).click();

    // Wait for the charger to appear in the list
    wait.until(
        ExpectedConditions.textToBePresentInElementLocated(By.cssSelector(".modal ul"), "AC"));
  }

  @When("I navigate to the admin operators page")
  public void iNavigateToTheAdminOperatorsPage() {
    try {
      WebElement closeButton = driver.findElement(By.cssSelector(".modal .cancel-button"));
      closeButton.click();
      // Wait for it to disappear
      wait.until(ExpectedConditions.invisibilityOfElementLocated(By.cssSelector(".modal")));
    } catch (Exception e) {
      // Modal wasn’t open? No problem, just ignore!
    }

    driver.findElement(By.xpath("//li[text()='Show Operators']")).click();
  }

  @When("I assign the station to an operator")
  public void iAssignTheStationToAnOperator() {

    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    wait.until(
        ExpectedConditions.visibilityOfElementLocated(
            By.cssSelector(".operator-card:not(.create-card)")));

    // Find operator cards (excluding .create-card)
    List<WebElement> operatorCards =
        driver.findElements(By.cssSelector(".operator-card:not(.create-card)"));

    if (operatorCards.isEmpty()) {
      throw new IllegalStateException("No operator cards found. Are you sure they exist?");
    }

    // Just click on the first actual operator
    operatorCards.get(0).click();

    WebElement modal = driver.findElement(By.cssSelector(".modal"));
    WebElement dropdown = modal.findElement(By.tagName("select"));

    Select select = new Select(dropdown);

    // Select the station from dropdown
    select.selectByVisibleText("Test Station");

    // Submit assignment
    driver.findElement(By.cssSelector("form button[type='submit']")).click();
  }

  @Then("I should see a success message")
  public void iShouldSeeASuccessMessage() {
    WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
    WebElement successMessage =
        wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//div[contains(@class, 'modal') and contains(., 'assigned')]")));
    assertTrue(
        successMessage.getText().contains("assigned")
            || successMessage.getText().contains("successfully"));
  }
}
