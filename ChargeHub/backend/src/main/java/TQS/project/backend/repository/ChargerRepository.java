package TQS.project.backend.repository;

import TQS.project.backend.entity.Charger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChargerRepository extends JpaRepository<Charger, Long> {
  // You can later add a JPQL query here for better performance
}
