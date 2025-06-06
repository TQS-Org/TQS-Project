package TQS.project.backend.repository;

import TQS.project.backend.entity.Booking;
import TQS.project.backend.entity.ChargingSession;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChargingSessionRepository extends JpaRepository<ChargingSession, Long> {
  boolean existsByBooking(Booking booking);

  ChargingSession findByBookingId(Long bookingId);
}
