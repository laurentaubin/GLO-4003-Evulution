package ca.ulaval.glo4003.ws.api.user;

import ca.ulaval.glo4003.ws.api.user.response.LoginResponse;
import ca.ulaval.glo4003.ws.service.user.dto.SessionDto;

public class LoginUserDtoAssembler {

  public LoginResponse assemble(SessionDto sessionDto) {
    var loginResponse = new LoginResponse();
    loginResponse.setToken(sessionDto.token);
    return loginResponse;
  }
}
