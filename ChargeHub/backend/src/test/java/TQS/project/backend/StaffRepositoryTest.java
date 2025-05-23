package TQS.project.backend;

import TQS.project.backend.entity.Staff;
import TQS.project.backend.repository.StaffRepository;
import app.getxray.xray.junit.customjunitxml.annotations.Requirement;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class StaffRepositoryTest {

  @Autowired private StaffRepository staffRepository;

  @Test
  @Requirement("SCRUM-41")
  void whenFindByMail_thenReturnStaff() {
    Staff staff = new Staff();
    staff.setMail("admin@example.com");
    staff.setName("Admin User");

    staffRepository.save(staff);

    Optional<Staff> found = staffRepository.findByMail("admin@example.com");

    assertTrue(found.isPresent());
    assertEquals("Admin User", found.get().getName());
  }

  @Test
  @Requirement("SCRUM-41")
  void whenFindByMail_notFound_thenEmptyOptional() {
    Optional<Staff> found = staffRepository.findByMail("nonexistent@example.com");
    assertTrue(found.isEmpty());
  }
}
