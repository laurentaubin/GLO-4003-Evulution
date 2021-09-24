package ca.ulaval.glo4003.ws.api.customer;

import ca.ulaval.glo4003.ws.api.customer.dto.LoginResponseDto;
import ca.ulaval.glo4003.ws.domain.auth.LoginToken;

public class LoginResponseAssembler {
  public LoginResponseDto assemble(LoginToken loginToken) {
    LoginResponseDto loginResponseDto = new LoginResponseDto();
    loginResponseDto.setToken(loginToken.getTokenValue());

    return loginResponseDto;
  }
}
