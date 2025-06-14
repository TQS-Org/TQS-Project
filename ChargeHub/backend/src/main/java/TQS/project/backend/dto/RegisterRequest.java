package TQS.project.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

public class RegisterRequest {

  @NotBlank(message = "Name is required")
  private String name;

  @Min(value = 18, message = "Age must be at least 18")
  @Max(value = 120, message = "Age must be realistic")
  private int age;

  @Pattern(regexp = "^9\\d{8}$", message = "Phone number must start with 9 and be exactly 9 digits")
  private String number;

  @NotBlank(message = "Email is required")
  @Email(message = "Email should be valid")
  private String mail;

  @Size(min = 8, message = "Password must be at least 8 characters")
  @Pattern(
      regexp = "^(?=.*[A-Za-z])(?=.*\\d).{8,}$",
      message = "Password must contain letters and numbers")
  private String password;

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
