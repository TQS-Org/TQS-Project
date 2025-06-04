package TQS.project.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class CreateBookingDTO {

  @NotBlank(message = "Email is required")
  @Email(message = "Email should be valid")
  private String mail;

  @NotNull(message = "Charger ID is mandatory")
  private Long chargerId;

  @NotNull(message = "Start time is mandatory")
  private LocalDateTime startTime;

  @Min(value = 5, message = "Duration must be at least 5 minutes")
  @Max(value = 60, message = "Duration must be at most 60 minutes")
  private int duration;

  // Getters and Setters
  public String getMail() {
    return mail;
  }

  public void setMail(String mail) {
    this.mail = mail;
  }

  public Long getChargerId() {
    return chargerId;
  }

  public void setChargerId(Long chargerId) {
    this.chargerId = chargerId;
  }

  public LocalDateTime getStartTime() {
    return startTime;
  }

  public void setStartTime(LocalDateTime startTime) {
    this.startTime = startTime;
  }

  public int getDuration() {
    return duration;
  }

  public void setDuration(int duration) {
    this.duration = duration;
  }
}
