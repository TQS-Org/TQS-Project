package TQS.project.backend.entity;

import jakarta.persistence.*;

@Entity
public class Client {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;
  private String password;
  private int age;

  @Column(unique = true)
  private String mail;

  private String number;

  public Client(String name, String password, int age, String mail, String number) {
    this.name = name;
    this.password = password;
    this.age = age;
    this.mail = mail;
    this.number = number;
  }

  public Client() {}

  public Client(String password, String mail) {
    this.password = password;
    this.mail = mail;
  }

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
}
