package TQS.project.backend;

import TQS.project.backend.entity.Client;
import TQS.project.backend.entity.Role;
import TQS.project.backend.entity.Staff;
import TQS.project.backend.repository.ClientRepository;
import TQS.project.backend.repository.StaffRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@Profile("never")
public class DataInitializer {

  @Bean
  CommandLineRunner init(
      ClientRepository clientRepo, StaffRepository staffRepo, PasswordEncoder encoder) {
    return args -> {
      if (clientRepo.findByMail("driver@mail.com").isEmpty()) {
        Client client =
            new Client(
                "Driver One", encoder.encode("driverpass"), 30, "driver@mail.com", "123456789");
        clientRepo.save(client);
      }

      if (staffRepo.findByMail("operator@mail.com").isEmpty()) {
        Staff operator = new Staff();
        operator.setName("Operator One");
        operator.setMail("operator@mail.com");
        operator.setPassword(encoder.encode("operatorpass"));
        operator.setAge(35);
        operator.setRole(Role.OPERATOR);
        operator.setActive(true);
        staffRepo.save(operator);
      }

      if (staffRepo.findByMail("admin@mail.com").isEmpty()) {
        Staff admin = new Staff();
        admin.setName("Admin One");
        admin.setMail("admin@mail.com");
        admin.setPassword(encoder.encode("adminpass"));
        admin.setAge(40);
        admin.setRole(Role.ADMIN);
        admin.setActive(true);
        staffRepo.save(admin);
      }
    };
  }
}
