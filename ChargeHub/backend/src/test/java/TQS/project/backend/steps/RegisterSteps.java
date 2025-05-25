package TQS.project.backend.steps;

import io.cucumber.java.Before;
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

  @When("I click on {string}")
  public void i_click_on(String id) {
    WebElement element = wait.until(ExpectedConditions.elementToBeClickable(By.id(id)));
    element.click();
  }

  @When("I fill in the registration form with:")
  public void i_fill_the_form(Map<String, String> data) {
    if (data.containsKey("name"))
      driver.findElement(By.cssSelector("input[placeholder='Name']")).sendKeys(data.get("name"));

    if (data.containsKey("age"))
      driver.findElement(By.cssSelector("input[placeholder='Age']")).sendKeys(data.get("age"));

    if (data.containsKey("number"))
      driver
          .findElement(By.cssSelector("input[placeholder='Phone Number']"))
          .sendKeys(data.get("number"));

    if (data.containsKey("email"))
      driver.findElement(By.cssSelector("input[placeholder='Email']")).sendKeys(data.get("email"));

    if (data.containsKey("password"))
      driver
          .findElement(By.cssSelector("input[placeholder='Password']"))
          .sendKeys(data.get("password"));

    if (data.containsKey("confirm password"))
      driver
          .findElement(By.cssSelector("input[placeholder='Confirm Password']"))
          .sendKeys(data.get("confirm password"));

    if (data.containsKey("address"))
      driver
          .findElement(By.cssSelector("input[placeholder='Address']"))
          .sendKeys(data.get("address"));
  }

  @When("I click the Sign Up button")
  public void i_click_the_button() {
    WebElement loginButton =
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".signup-button")));
    loginButton.click();
  }
}
