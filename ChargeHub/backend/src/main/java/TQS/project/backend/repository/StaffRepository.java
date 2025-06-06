package TQS.project.backend.repository;

import TQS.project.backend.entity.Staff;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;
import TQS.project.backend.entity.Role;

@Repository
public interface StaffRepository extends JpaRepository<Staff, Long> {
  Optional<Staff> findByMail(String email);

  List<Staff> findByRole(Role role);

  Optional<Staff> findByAssignedStationId(Long stationId);
}
