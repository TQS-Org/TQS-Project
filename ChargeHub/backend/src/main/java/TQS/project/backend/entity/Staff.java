package TQS.project.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Staff {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;
  private String password;
  private int age;
  private String mail;
  private String number;
  private String address;
  private boolean isActive;
  private LocalDate startDate;
  private LocalDate endDate;

  @Enumerated(EnumType.STRING)
  private Role role;

  @OneToOne
  @JoinColumn(name = "station_id", referencedColumnName = "id")
  private Station assignedStation;

  public Staff(String mail, String password) {
    this.mail = mail;
    this.password = password;
  }

  public Staff() {}

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public int getAge() {
    return age;
  }

  public void setAge(int age) {
    this.age = age;
  }

  public String getMail() {
    return mail;
  }

  public void setMail(String mail) {
    this.mail = mail;
  }

  public String getNumber() {
    return number;
  }

  public void setNumber(String number) {
    this.number = number;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public boolean isActive() {
    return isActive;
  }

  public void setActive(boolean isActive) {
    this.isActive = isActive;
  }

  public LocalDate getStartDate() {
    return startDate;
  }

  public void setStartDate(LocalDate startDate) {
    this.startDate = startDate;
  }

  public LocalDate getEndDate() {
    return endDate;
  }

  public void setEndDate(LocalDate endDate) {
    this.endDate = endDate;
  }

  public Role getRole() {
    return role;
  }

  public void setRole(Role role) {
    this.role = role;
  }

  public Station getAssignedStation() {
    return assignedStation;
  }

  public void setAssignedStation(Station assignedStation) {
    this.assignedStation = assignedStation;
  }
}
