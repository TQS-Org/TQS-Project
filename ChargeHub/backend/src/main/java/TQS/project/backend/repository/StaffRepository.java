package TQS.project.backend.repository;

import TQS.project.backend.entity.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface StaffRepository extends JpaRepository<Staff, Long> {
  Optional<Staff> findByMail(String email);
}
