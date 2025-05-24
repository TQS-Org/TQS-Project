package TQS.project.backend.steps;

import io.cucumber.java.Before;
import io.cucumber.java.en.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import static org.junit.jupiter.api.Assertions.*;

public class CreateOperatorSteps {

  private WebDriver driver;
  private WebDriverWait wait;

  @Before
  public void setup() {
    WebDriverSingleton.initialize();
    driver = WebDriverSingleton.getDriver();
    wait = WebDriverSingleton.getWait();
  }

  @And("I should see a list of operators")
  public void i_should_see_list_of_operators() {
    wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("operator-card")));
    assertFalse(driver.findElements(By.className("operator-card")).isEmpty());
  }

  @When("I click on Create Operator")
  public void i_click_on_create_operator() {
    WebElement button =
        driver.findElements(By.className("create-card")).stream()
            .filter(el -> el.getText().contains("Create Operator"))
            .findFirst()
            .orElseThrow();
    button.click();
  }

  @Then("I should see a success modal with the message {string}")
  public void i_should_see_success_modal(String expectedMessage) {
    WebElement modal =
        wait.until(
            ExpectedConditions.visibilityOfElementLocated(By.className("success-animation")));
    assertTrue(modal.getText().contains(expectedMessage));
  }
}
