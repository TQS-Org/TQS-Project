package TQS.project.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "charging_session")
public class ChargingSession {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "booking", nullable = false)
  @JsonIgnore
  private Booking booking;

  @Column(nullable = false)
  private LocalDateTime startTime;

  @Column(nullable = false)
  private LocalDateTime endTime;

  private float energyConsumed;

  private float price;

  @Column(nullable = false)
  private String sessionStatus;

  // Constructors
  public ChargingSession() {}

  public ChargingSession(
      Booking booking,
      LocalDateTime startTime,
      LocalDateTime endTime,
      float energyConsumed,
      float price,
      String sessionStatus) {
    this.booking = booking;
    this.startTime = startTime;
    this.endTime = endTime;
    this.energyConsumed = energyConsumed;
    this.price = price;
    this.sessionStatus = sessionStatus;
  }

  // Getters and Setters
  public long getId() {
    return id;
  }

  public Booking getBooking() {
    return booking;
  }

  public void setBooking(Booking booking) {
    this.booking = booking;
  }

  public LocalDateTime getStartTime() {
    return startTime;
  }

  public void setStartTime(LocalDateTime startTime) {
    this.startTime = startTime;
  }

  public LocalDateTime getEndTime() {
    return endTime;
  }

  public void setEndTime(LocalDateTime endTime) {
    this.endTime = endTime;
  }

  public float getEnergyConsumed() {
    return energyConsumed;
  }

  public void setEnergyConsumed(float energyConsumed) {
    this.energyConsumed = energyConsumed;
  }

  public float getPrice() {
    return price;
  }

  public void setPrice(float price) {
    this.price = price;
  }

  public String getSessionStatus() {
    return sessionStatus;
  }

  public void setSessionStatus(String sessionStatus) {
    this.sessionStatus = sessionStatus;
  }

  public void setId(long id) {
    this.id = id;
  }
}
