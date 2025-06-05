package TQS.project.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class StationDTO {
  @NotBlank private String name;

  @NotBlank private String brand;

  @NotNull private Double latitude;

  @NotNull private Double longitude;

  @NotBlank private String address;

  private Integer numberOfChargers;

  private String openingHours;
  private String closingHours;

  private Double price;

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

  public Double getLatitude() {
    return latitude;
  }

  public void setLatitude(Double latitude) {
    this.latitude = latitude;
  }

  public Double getLongitude() {
    return longitude;
  }

  public void setLongitude(Double longitude) {
    this.longitude = longitude;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public Integer getNumberOfChargers() {
    return numberOfChargers;
  }

  public void setNumberOfChargers(Integer numberOfChargers) {
    this.numberOfChargers = numberOfChargers;
  }

  public String getOpeningHours() {
    return openingHours;
  }

  public void setOpeningHours(String openingHours) {
    this.openingHours = openingHours;
  }

  public String getClosingHours() {
    return closingHours;
  }

  public void setClosingHours(String closingHours) {
    this.closingHours = closingHours;
  }

  public Double getPrice() {
    return price;
  }

  public void setPrice(Double price) {
    this.price = price;
  }
}
