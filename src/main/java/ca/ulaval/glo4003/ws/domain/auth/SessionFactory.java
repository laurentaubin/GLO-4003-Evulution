package ca.ulaval.glo4003.ws.domain.auth;

import java.util.UUID;

public class SessionFactory {
  public Session create(String email) {
    return new Session(UUID.randomUUID().toString(), email);
  }

  public Session create(String token, String email) {
    return new Session(token, email);
  }
}
