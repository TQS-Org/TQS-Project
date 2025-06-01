package TQS.project.backend.repository;

import TQS.project.backend.entity.Booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
  Optional<Booking> findById(long id);
  @Query("SELECT b FROM Booking b WHERE b.charger.station.id = :stationId")
  List<Booking> findAllBookingsByStationId(@Param("stationId") long stationId);

  @Query("SELECT b FROM Booking b WHERE b.charger.station.id = :stationId AND DATE(b.startTime) = :date")
  List<Booking> findByStationIdAndDate(@Param("stationId") long stationId, @Param("date") LocalDate date);
}
