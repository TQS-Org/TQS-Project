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
    if (data.containsKey("name")) {
      WebElement nameField =
          wait.until(
              ExpectedConditions.visibilityOfElementLocated(
                  By.cssSelector("input[placeholder='Name']")));
      nameField.sendKeys(data.get("name"));
    }

    if (data.containsKey("age")) {
      WebElement ageField =
          wait.until(
              ExpectedConditions.visibilityOfElementLocated(
                  By.cssSelector("input[placeholder='Age']")));
      ageField.sendKeys(data.get("age"));
    }

    if (data.containsKey("number")) {
      WebElement numberField =
          wait.until(
              ExpectedConditions.visibilityOfElementLocated(
                  By.cssSelector("input[placeholder='Phone Number']")));
      numberField.sendKeys(data.get("number"));
    }

    if (data.containsKey("email")) {
      WebElement emailField =
          wait.until(
              ExpectedConditions.visibilityOfElementLocated(
                  By.cssSelector("input[placeholder='Email']")));
      emailField.sendKeys(data.get("email"));
    }

    if (data.containsKey("password")) {
      WebElement passwordField =
          wait.until(
              ExpectedConditions.visibilityOfElementLocated(
                  By.cssSelector("input[placeholder='Password']")));
      passwordField.sendKeys(data.get("password"));
    }

    if (data.containsKey("confirm password")) {
      WebElement confirmPasswordField =
          wait.until(
              ExpectedConditions.visibilityOfElementLocated(
                  By.cssSelector("input[placeholder='Confirm Password']")));
      confirmPasswordField.sendKeys(data.get("confirm password"));
    }

    if (data.containsKey("address")) {
      WebElement addressField =
          wait.until(
              ExpectedConditions.visibilityOfElementLocated(
                  By.cssSelector("input[placeholder='Address']")));
      addressField.sendKeys(data.get("address"));
    }
  }

  @When("I click the Sign Up button")
  public void i_click_the_button() {
    WebElement signupButton =
        wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".signup-button")));
    signupButton.click();
  }
}
