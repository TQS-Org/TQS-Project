package TQS.project.backend.entity;

import jakarta.persistence.*;

@Entity
public class Station {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;
  private String brand;
  private double latitude;
  private double longitude;
  private String address;
  private int numberOfChargers;
  private String openingHours;
  private String closingHours;
  private double price;

  public Station(
      String name,
      String brand,
      double latitude,
      double longitude,
      String address,
      int numberOfChargers,
      String openingHours,
      String closingHours,
      double price) {
    this.name = name;
    this.brand = brand;
    this.latitude = latitude;
    this.longitude = longitude;
    this.address = address;
    this.numberOfChargers = numberOfChargers;
    this.openingHours = openingHours;
    this.closingHours = closingHours;
    this.price = price;
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

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getBrand() {
    return brand;
  }

  public void setBrand(String brand) {
    this.brand = brand;
  }

  public String getClosingHours() {
    return closingHours;
  }

  public void setClosingHours(String closingHours) {
    this.closingHours = closingHours;
  }
}
