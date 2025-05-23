package TQS.project.backend.steps;

import io.github.bonigarcia.wdm.WebDriverManager;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class WebDriverSingleton {
  private static WebDriver driver;
  private static WebDriverWait wait;

  public static void initialize() {
    if (driver == null) {
      WebDriverManager.chromedriver().setup();

      ChromeOptions options = new ChromeOptions();
      options.addArguments(
          "--headless=new"); // Use "--headless=new" for better support in newer Chrome
      options.addArguments("--no-sandbox");
      options.addArguments("--disable-dev-shm-usage");
      options.addArguments("--disable-gpu");
      options.addArguments("--window-size=1200,800");

      driver = new ChromeDriver(options);
      wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }
  }

  public static WebDriver getDriver() {
    return driver;
  }

  public static WebDriverWait getWait() {
    return wait;
  }

  public static void quit() {
    if (driver != null) {
      driver.quit();
      driver = null;
      wait = null;
    }
  }
}
