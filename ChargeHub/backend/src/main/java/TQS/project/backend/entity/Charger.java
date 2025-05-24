package TQS.project.backend.entity;

import jakarta.persistence.*;

@Entity
public class Charger {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne
  @JoinColumn(name = "station_id")
  private Station station;

  private String type; // AC, DC, etc.
  private double power; // in kW
  private Boolean available; // devia ser status btw
  private String connectorType; // CCS, CHAdeMO, Type 2, etc.

  public Charger(String type, double power, Boolean available, String connectorType) {
    this.type = type;
    this.power = power;
    this.available = available;
    this.connectorType = connectorType;
  }

  public Charger() {}

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Station getStation() {
    return station;
  }

  public void setStation(Station station) {
    this.station = station;
  }

  public String getType() {
    return type;
  }

  public Boolean getAvailable() {
    return available;
  }

  public void setAvailable(Boolean available) {
    this.available = available;
  }

  public void setType(String type) {
    this.type = type;
  }

  public double getPower() {
    return power;
  }

  public void setPower(double power) {
    this.power = power;
  }

  public String getConnectorType() {
    return connectorType;
  }

  public void setConnectorType(String connectorType) {
    this.connectorType = connectorType;
  }
}
