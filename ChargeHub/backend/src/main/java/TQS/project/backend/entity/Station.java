package TQS.project.backend.entity;

import java.util.List;

import jakarta.persistence.*;

@Entity
public class Station {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private double latitude;
  private double longitude;
  private String address;
  private int numberOfChargers;
  private String openingHours;
  private double price;

  @OneToMany(mappedBy = "assignedStation")
  private List<Staff> assignedStaff;

  public Station(
      double latitude,
      double longitude,
      String address,
      int numberOfChargers,
      String openingHours,
      List<Staff> assignedStaff) {
    this.latitude = latitude;
    this.longitude = longitude;
    this.address = address;
    this.numberOfChargers = numberOfChargers;
    this.openingHours = openingHours;
    this.assignedStaff = assignedStaff;
  }

  public Station() {}

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public double getLatitude() {
    return latitude;
  }

  public void setLatitude(double latitude) {
    this.latitude = latitude;
  }

  public double getLongitude() {
    return longitude;
  }

  public void setLongitude(double longitude) {
    this.longitude = longitude;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public int getNumberOfChargers() {
    return numberOfChargers;
  }

  public void setNumberOfChargers(int numberOfChargers) {
    this.numberOfChargers = numberOfChargers;
  }

  public String getOpeningHours() {
    return openingHours;
  }

  public void setOpeningHours(String openingHours) {
    this.openingHours = openingHours;
  }

  public double getPrice() {
    return price;
  }

  public void setPrice(double price) {
    this.price = price;
  }

  public List<Staff> getAssignedStaff() {
    return assignedStaff;
  }

  public void setAssignedStaff(List<Staff> assignedStaff) {
    this.assignedStaff = assignedStaff;
  }
}
