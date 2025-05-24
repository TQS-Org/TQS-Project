package TQS.project.backend.repository;

import TQS.project.backend.entity.Station;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StationRepository extends JpaRepository<Station, Long> {
  // custom query methods can be added here later if needed
}
