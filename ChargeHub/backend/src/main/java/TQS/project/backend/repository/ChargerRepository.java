package TQS.project.backend.repository;

import TQS.project.backend.entity.Charger;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import TQS.project.backend.entity.Station;
import java.util.List;

@Repository
public interface ChargerRepository extends JpaRepository<Charger, Long> {
  List<Charger> findAllByStationId(long id);

  List<Charger> findByStation(Station station);
}
