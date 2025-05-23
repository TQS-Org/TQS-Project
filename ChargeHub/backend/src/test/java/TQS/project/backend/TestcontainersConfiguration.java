package TQS.project.backend;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@TestConfiguration
public class TestcontainersConfiguration
    implements ApplicationContextInitializer<ConfigurableApplicationContext> {

  @Container
  public static MySQLContainer<?> mysqlContainer =
      new MySQLContainer<>("mysql:8.0")
          .withDatabaseName("tqs_db")
          .withUsername("admin")
          .withPassword("admin");

  @Override
  public void initialize(ConfigurableApplicationContext context) {
    mysqlContainer.start();

    TestPropertyValues.of(
            "spring.datasource.url=" + mysqlContainer.getJdbcUrl(),
            "spring.datasource.username=" + mysqlContainer.getUsername(),
            "spring.datasource.password=" + mysqlContainer.getPassword(),
            "spring.datasource.driver-class-name=" + mysqlContainer.getDriverClassName())
        .applyTo(context.getEnvironment());
  }
}
