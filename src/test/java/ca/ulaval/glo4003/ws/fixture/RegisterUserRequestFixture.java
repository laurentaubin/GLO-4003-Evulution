package ca.ulaval.glo4003.ws.fixture;

import ca.ulaval.glo4003.ws.api.user.request.RegisterUserRequest;

public class RegisterUserRequestFixture {
  private String name = "a name";
  private String birthDate = "1999-05-05";
  private String email = "an@email.com";
  private String password = "a password";
  private String sex = "a sex";

  public RegisterUserRequest build() {
    RegisterUserRequest registerUserDto = new RegisterUserRequest();
    registerUserDto.setName(name);
    registerUserDto.setBirthDate(birthDate);
    registerUserDto.setEmail(email);
    registerUserDto.setPassword(password);
    registerUserDto.setSex(sex);

    return registerUserDto;
  }

  public RegisterUserRequestFixture withName(String name) {
    this.name = name;
    return this;
  }

  public RegisterUserRequestFixture withBirthDate(String birthDate) {
    this.birthDate = birthDate;
    return this;
  }

  public RegisterUserRequestFixture withEmail(String email) {
    this.email = email;
    return this;
  }

  public RegisterUserRequestFixture withPassword(String password) {
    this.password = password;
    return this;
  }

  public RegisterUserRequestFixture withSex(String sex) {
    this.sex = sex;
    return this;
  }
}
