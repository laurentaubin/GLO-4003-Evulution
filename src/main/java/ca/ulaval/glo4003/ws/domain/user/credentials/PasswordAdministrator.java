package ca.ulaval.glo4003.ws.domain.user.credentials;

import java.util.Optional;

public class PasswordAdministrator {
  private final PasswordRegistry passwordRegistry;

  public PasswordAdministrator(PasswordRegistry passwordRegistry) {
    this.passwordRegistry = passwordRegistry;
  }

  public void register(String userEmail, String password) {
    passwordRegistry.save(userEmail, password);
  }

  public boolean areCredentialsValid(String userEmail, String password) {
    Optional<String> retrievedPassword = passwordRegistry.retrievePassword(userEmail);
    return !match(retrievedPassword, password);
  }

  private boolean match(Optional<String> retrievedPassword, String givenPassword) {
    return retrievedPassword.isEmpty() || !retrievedPassword.get().equals(givenPassword);
  }
}
