package ca.ulaval.glo4003.ws.service.user.dto;

public class RegisterUserDto {

  private final String name;
  private final String birthDate;
  private final String sex;
  private final String email;
  private final String password;

  public RegisterUserDto(String name, String birthDate, String sex, String email, String password) {
    this.name = name;
    this.birthDate = birthDate;
    this.sex = sex;
    this.email = email;
    this.password = password;
  }

  public String getName() {
    return name;
  }

  public String getBirthDate() {
    return birthDate;
  }

  public String getSex() {
    return sex;
  }

  public String getEmail() {
    return email;
  }

  public String getPassword() {
    return password;
  }
}
