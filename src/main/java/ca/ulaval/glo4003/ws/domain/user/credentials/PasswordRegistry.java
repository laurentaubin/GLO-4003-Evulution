package ca.ulaval.glo4003.ws.domain.user.credentials;

import java.util.Optional;

public interface PasswordRegistry {
  void save(String userEmail, String password);

  Optional<String> retrievePassword(String userEmail);
}
