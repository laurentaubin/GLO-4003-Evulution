package ca.ulaval.glo4003.ws.domain.auth;

import java.util.UUID;

public class SessionTokenGenerator {
  public SessionToken generate() {
    return new SessionToken(UUID.randomUUID().toString());
  }

  public SessionToken generate(String tokenValue) {
    return new SessionToken(tokenValue);
  }
}
