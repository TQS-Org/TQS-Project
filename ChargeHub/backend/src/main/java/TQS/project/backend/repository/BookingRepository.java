package TQS.project.backend.repository;

import TQS.project.backend.entity.Booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
  Optional<Booking> findById(long id);

  Optional<Booking> findByToken(String token);

  @Query("SELECT b FROM Booking b WHERE b.charger.id = :chargerId")
  List<Booking> findAllBookingsByStationId(@Param("chargerId") long stationId);

  List<Booking> findAllBookingsByChargerId(long chargerId);

  @Query(
      "SELECT b FROM Booking b WHERE b.charger.id = :chargerId AND b.startTime >= :start AND"
          + " b.startTime < :end")
  List<Booking> findByChargerIdAndDate(
      @Param("chargerId") long chargerId,
      @Param("start") LocalDateTime start,
      @Param("end") LocalDateTime end);

  List<Booking> findAllByUserId(long clientId);
}
