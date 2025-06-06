package TQS.project.backend.dto;

import java.time.LocalDateTime;

public class FinishedChargingSessionDTO {

  private float energyConsumed;
  private LocalDateTime endTime;

  // Constructors
  public FinishedChargingSessionDTO() {}

  public FinishedChargingSessionDTO(float energyConsumed, LocalDateTime endTime) {
    this.energyConsumed = energyConsumed;
    this.endTime = endTime;
  }

  // Getters and Setters
  public float getEnergyConsumed() {
    return energyConsumed;
  }

  public void setEnergyConsumed(float energyConsumed) {
    this.energyConsumed = energyConsumed;
  }

  public LocalDateTime getEndTime() {
    return endTime;
  }

  public void setEndTime(LocalDateTime endTime) {
    this.endTime = endTime;
  }
}
