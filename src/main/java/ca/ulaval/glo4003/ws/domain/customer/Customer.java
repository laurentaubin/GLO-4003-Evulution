package ca.ulaval.glo4003.ws.domain.customer;

public class Customer {
  private final String name;
  private final BirthDate birthDate;
  private final String sex;
  private final String email;
  private final String password;

  public Customer(String name, BirthDate birthDate, String sex, String email, String password) {
    this.name = name;
    this.birthDate = birthDate;
    this.sex = sex;
    this.email = email;
    this.password = password;
  }

  public String getName() {
    return name;
  }

  public BirthDate getBirthDate() {
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
