package TQS.project.backend.steps;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;

public class UpdateSteps {

  private WebDriver driver;
  private WebDriverWait wait;

  @Before
  public void setup() {
    WebDriverSingleton.initialize();
    driver = WebDriverSingleton.getDriver();
    wait = WebDriverSingleton.getWait();
  }

  @And("I update the station name to {string}")
  public void iUpdateTheStationName(String newName) {
    WebElement nameField = driver.findElement(By.name("name"));
    nameField.clear();
    nameField.sendKeys(newName);
  }

  @And("I update the brand to {string}")
  public void iUpdateTheBrand(String newBrand) {
    WebElement brandField = driver.findElement(By.name("brand"));
    brandField.clear();
    brandField.sendKeys(newBrand);
  }

  @And("I update the address to {string}")
  public void iUpdateTheAddress(String newAddress) {
    WebElement addressField = driver.findElement(By.name("address"));
    addressField.clear();
    addressField.sendKeys(newAddress);
  }

  @Then("I should see a message saying {string}")
  public void iShouldSeeAMessageSaying(String expectedMessage) {
    WebElement messageElement = driver.findElement(By.className("message"));
    String actualMessage = messageElement.getText();
    assertEquals(expectedMessage, actualMessage);
  }

  @Then("I should see the updated details for the station")
  public void iShouldSeeTheUpdatedDetailsForTheStation() {
    WebElement nameElement = driver.findElement(By.xpath("//p[strong[text()='Name:']]"));
    assertTrue(nameElement.getText().contains("Updated Station Name"));

    WebElement brandElement = driver.findElement(By.xpath("//p[strong[text()='Brand:']]"));
    assertTrue(brandElement.getText().contains("Updated Brand"));

    WebElement addressElement = driver.findElement(By.xpath("//p[strong[text()='Address:']]"));
    assertTrue(addressElement.getText().contains("Updated Address, City"));
  }
}
