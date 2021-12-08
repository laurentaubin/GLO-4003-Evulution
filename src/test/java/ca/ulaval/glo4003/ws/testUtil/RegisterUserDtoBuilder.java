package ca.ulaval.glo4003.ws.testUtil;

import ca.ulaval.glo4003.ws.service.user.dto.RegisterUserDto;

public class RegisterUserDtoBuilder {
  private String name = "a name";
  private String birthDate = "1999-05-05";
  private String email = "an@email.com";
  private String password = "a password";
  private String sex = "a sex";

  public RegisterUserDto build() {
    RegisterUserDto registerUserDto = new RegisterUserDto();
    registerUserDto.name = name;
    registerUserDto.birthDate = birthDate;
    registerUserDto.email = email;
    registerUserDto.password = password;
    registerUserDto.sex = sex;

    return registerUserDto;
  }

  public RegisterUserDtoBuilder withName(String name) {
    this.name = name;
    return this;
  }

  public RegisterUserDtoBuilder withBirthDate(String birthDate) {
    this.birthDate = birthDate;
    return this;
  }

  public RegisterUserDtoBuilder withEmail(String email) {
    this.email = email;
    return this;
  }

  public RegisterUserDtoBuilder withPassword(String password) {
    this.password = password;
    return this;
  }

  public RegisterUserDtoBuilder withSex(String sex) {
    this.sex = sex;
    return this;
  }
}
