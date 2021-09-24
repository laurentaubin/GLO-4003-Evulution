package ca.ulaval.glo4003.ws.testUtil;

import ca.ulaval.glo4003.ws.domain.customer.BirthDate;
import ca.ulaval.glo4003.ws.domain.customer.Customer;
import java.time.LocalDate;

public class CustomerBuilder {
  private String name = "a name";
  private BirthDate birthDate = new BirthDate(LocalDate.of(4584, 7, 18));
  private String email = "an@email.com";
  private String password = "a password";
  private String sex = "a sex";

  public Customer build() {
    return new Customer(name, birthDate, sex, email, password);
  }

  public CustomerBuilder withName(String name) {
    this.name = name;
    return this;
  }

  public CustomerBuilder withBirthDate(BirthDate birthDate) {
    this.birthDate = birthDate;
    return this;
  }

  public CustomerBuilder withEmail(String email) {
    this.email = email;
    return this;
  }

  public CustomerBuilder withPassword(String password) {
    this.password = password;
    return this;
  }

  public CustomerBuilder withSex(String sex) {
    this.sex = sex;
    return this;
  }
}
