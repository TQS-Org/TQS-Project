package TQS.project.backend.repository;

import TQS.project.backend.entity.Charger;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChargerRepository extends JpaRepository<Charger, Long> {
  Optional<Charger> findById(long id);
  List<Charger> findAllByStationId(long id);
}
