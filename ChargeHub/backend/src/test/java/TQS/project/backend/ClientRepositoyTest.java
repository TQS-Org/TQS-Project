package TQS.project.backend;

import TQS.project.backend.entity.Client;
import TQS.project.backend.repository.ClientRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import app.getxray.xray.junit.customjunitxml.annotations.Requirement;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestPropertySource(
    properties = {"spring.flyway.enabled=false", "spring.jpa.hibernate.ddl-auto=create-drop"})
class ClientRepositoryTest {

  @Autowired private ClientRepository clientRepository;

  @Test
  @Requirement("SCRUM-41")
  void whenFindByMail_thenReturnClient() {
    Client client = new Client();
    client.setMail("jane.doe@example.com");
    client.setName("Jane Doe");
    client.setPassword("securepassword");

    clientRepository.save(client);

    Optional<Client> found = clientRepository.findByMail("jane.doe@example.com");

    assertTrue(found.isPresent());
    assertEquals("Jane Doe", found.get().getName());
  }

  @Test
  @Requirement("SCRUM-41")
  void whenFindByMail_notFound_thenEmptyOptional() {
    Optional<Client> found = clientRepository.findByMail("missing@example.com");
    assertTrue(found.isEmpty());
  }
}
