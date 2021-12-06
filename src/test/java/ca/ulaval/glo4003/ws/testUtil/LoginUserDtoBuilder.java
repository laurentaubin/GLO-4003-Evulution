package ca.ulaval.glo4003.ws.testUtil;

import ca.ulaval.glo4003.ws.service.user.dto.LoginUserDto;

public class LoginUserDtoBuilder {
  private String email = "email@email.com";
  private String password = "2312";

  public LoginUserDtoBuilder withEmail(String email) {
    this.email = email;
    return this;
  }

  public LoginUserDtoBuilder withPassword(String password) {
    this.password = password;
    return this;
  }

  public LoginUserDto build() {
    LoginUserDto loginUserDto = new LoginUserDto();
    loginUserDto.setEmail(email);
    loginUserDto.setPassword(password);

    return loginUserDto;
  }
}
