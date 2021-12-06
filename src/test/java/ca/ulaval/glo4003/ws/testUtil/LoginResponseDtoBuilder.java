package ca.ulaval.glo4003.ws.testUtil;

import ca.ulaval.glo4003.ws.service.user.dto.LoginResponseDto;

public class LoginResponseDtoBuilder {
  private String token = "token132";

  public LoginResponseDtoBuilder withToken(String token) {
    this.token = token;
    return this;
  }

  public LoginResponseDto build() {
    LoginResponseDto loginResponseDto = new LoginResponseDto();
    loginResponseDto.setToken(token);

    return loginResponseDto;
  }
}
