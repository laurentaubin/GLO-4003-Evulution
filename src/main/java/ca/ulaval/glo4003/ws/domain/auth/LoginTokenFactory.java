package ca.ulaval.glo4003.ws.domain.auth;

import java.util.UUID;

public class LoginTokenFactory {
  public LoginToken create() {
    return new LoginToken(UUID.randomUUID().toString());
  }

  public LoginToken create(String token) {
    return new LoginToken(token);
  }
}
