package ca.ulaval.glo4003.ws.api.user;

import ca.ulaval.glo4003.ws.api.user.dto.LoginResponseDto;
import ca.ulaval.glo4003.ws.domain.auth.Session;

public class LoginResponseAssembler {
  public LoginResponseDto assemble(Session session) {
    LoginResponseDto loginResponseDto = new LoginResponseDto();
    loginResponseDto.setToken(session.getToken().getTokenValue());

    return loginResponseDto;
  }
}
