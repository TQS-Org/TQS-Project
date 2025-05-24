package TQS.project.backend.repository;

import TQS.project.backend.entity.Staff;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Long> {
  Optional<Staff> findByMail(String email);
}
