package ca.ulaval.glo4003.ws.service.user;

import ca.ulaval.glo4003.ws.domain.auth.Session;
import ca.ulaval.glo4003.ws.service.user.dto.LoginResponseDto;

public class LoginResponseAssembler {
  public LoginResponseDto assemble(Session session) {
    LoginResponseDto loginResponseDto = new LoginResponseDto();
    loginResponseDto.setToken(session.getToken().getTokenValue());

    return loginResponseDto;
  }
}
