package ca.ulaval.glo4003.ws.testUtil;

import ca.ulaval.glo4003.ws.api.customer.dto.RegisterCustomerDto;

public class RegisterCustomerDtoBuilder {
  private String name = "a name";
  private String birthDate = "1999-05-05";
  private String email = "an@email.com";
  private String password = "a password";
  private String sex = "a sex";

  public RegisterCustomerDto build() {
    var registerCustomerDto = new RegisterCustomerDto();
    registerCustomerDto.setName(name);
    registerCustomerDto.setBirthDate(birthDate);
    registerCustomerDto.setEmail(email);
    registerCustomerDto.setPassword(password);
    registerCustomerDto.setSex(sex);

    return registerCustomerDto;
  }

  public RegisterCustomerDtoBuilder withName(String name) {
    this.name = name;
    return this;
  }

  public RegisterCustomerDtoBuilder withBirthDate(String birthDate) {
    this.birthDate = birthDate;
    return this;
  }

  public RegisterCustomerDtoBuilder withEmail(String email) {
    this.email = email;
    return this;
  }

  public RegisterCustomerDtoBuilder withPassword(String password) {
    this.password = password;
    return this;
  }

  public RegisterCustomerDtoBuilder withSex(String sex) {
    this.sex = sex;
    return this;
  }
}
