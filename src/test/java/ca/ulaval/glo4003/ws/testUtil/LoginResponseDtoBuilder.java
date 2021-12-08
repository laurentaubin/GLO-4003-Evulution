package ca.ulaval.glo4003.ws.testUtil;

import ca.ulaval.glo4003.ws.service.user.dto.SessionDto;

public class LoginResponseDtoBuilder {
  private String token = "token132";

  public LoginResponseDtoBuilder withToken(String token) {
    this.token = token;
    return this;
  }

  public SessionDto build() {
    SessionDto loginResponseDto = new SessionDto();
    loginResponseDto.token = token;

    return loginResponseDto;
  }
}
