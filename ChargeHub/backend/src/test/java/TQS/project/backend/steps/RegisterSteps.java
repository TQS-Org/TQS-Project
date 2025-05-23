package TQS.project.backend.steps;

import io.cucumber.java.Before;
import io.cucumber.java.After;
import io.cucumber.java.en.*;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

public class RegisterSteps {

  private WebDriver driver;
  private WebDriverWait wait;

  @Before
  public void setup() {
    WebDriverSingleton.initialize();
    driver = WebDriverSingleton.getDriver();
    wait = WebDriverSingleton.getWait();
  }

  @After
  public void cleanUp() {
    WebDriverSingleton.quit();
  }

  @When("I click on {string}")
  public void i_click_on(String id) {
    WebElement element = wait.until(ExpectedConditions.elementToBeClickable(By.id(id)));
    element.click();
  }

  @When("I fill in the registration form with:")
  public void i_fill_the_form(Map<String, String> data) {
    driver.findElement(By.cssSelector("input[placeholder='Name']")).sendKeys(data.get("name"));
    driver.findElement(By.cssSelector("input[placeholder='Age']")).sendKeys(data.get("age"));
    driver
        .findElement(By.cssSelector("input[placeholder='Phone Number']"))
        .sendKeys(data.get("number"));
    driver.findElement(By.cssSelector("input[placeholder='Email']")).sendKeys(data.get("email"));
    driver
        .findElement(By.cssSelector("input[placeholder='Password']"))
        .sendKeys(data.get("password"));
  }

  @When("I click the Sign Up button")
  public void i_click_the_button() {
    WebElement loginButton =
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".signup-button")));
    loginButton.click();
  }
}
