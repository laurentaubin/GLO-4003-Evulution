package ca.ulaval.glo4003.ws.infrastructure.user.credentials;

import ca.ulaval.glo4003.ws.domain.user.credentials.PasswordRegistry;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryPasswordRegistry implements PasswordRegistry {
  private final Map<String, String> userEmailToPassword = new HashMap<>();

  @Override
  public void save(String userEmail, String password) {
    userEmailToPassword.put(userEmail, password);
  }

  @Override
  public Optional<String> retrievePassword(String userEmail) {
    return Optional.ofNullable(userEmailToPassword.get(userEmail));
  }
}
