package TQS.project.backend.steps;

import io.cucumber.java.Before;
import io.cucumber.java.en.*;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.*;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class LoginSteps {
  private WebDriver driver;
  private WebDriverWait wait;

  @Before
  public void setup() {
    WebDriverSingleton.initialize();
    driver = WebDriverSingleton.getDriver();
    wait = WebDriverSingleton.getWait();
  }

  @Given("I am on the login page")
  public void iAmOnTheLoginPage() {
    driver.get("http://localhost:3000/");
    System.out.println("Current URL: " + driver.getCurrentUrl());
    assertTrue(driver.getCurrentUrl().equals("http://localhost:3000/"));
  }

  @When("I enter {string} and {string}")
  public void iEnterEmailAndPassword(String email, String password) {
    wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".login-input")));
    driver.findElements(By.cssSelector(".login-input")).get(0).sendKeys(email); // Email field
    driver.findElements(By.cssSelector(".login-input")).get(1).sendKeys(password); // Password field
  }

  @And("I click the login button")
  public void iClickLoginButton() {
    WebElement loginButton =
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".login-button")));
    loginButton.click();
  }

  @Then("I should be redirected to the {string} page")
  public void iShouldBeRedirectedToDashboard(String expectedPath) {
    wait.until(webDriver -> webDriver.getCurrentUrl().contains(expectedPath));
    String currentUrl = driver.getCurrentUrl();
    assertTrue(
        currentUrl.contains(expectedPath),
        "Expected to be on: " + expectedPath + " but was: " + currentUrl);
  }
}
