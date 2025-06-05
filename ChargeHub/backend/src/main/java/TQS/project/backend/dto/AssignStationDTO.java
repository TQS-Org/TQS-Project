package TQS.project.backend.dto;

import jakarta.validation.constraints.NotNull;

public class AssignStationDTO {

  @NotNull private Long operatorId;

  @NotNull private Long stationId;

  public Long getOperatorId() {
    return operatorId;
  }

  public void setOperatorId(Long operatorId) {
    this.operatorId = operatorId;
  }

  public Long getStationId() {
    return stationId;
  }

  public void setStationId(Long stationId) {
    this.stationId = stationId;
  }
}
