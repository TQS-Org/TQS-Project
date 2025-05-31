package TQS.project.backend.repository;

import TQS.project.backend.entity.Booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
  Optional<Booking> findById(long id);
}
