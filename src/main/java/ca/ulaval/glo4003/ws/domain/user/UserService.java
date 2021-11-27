package ca.ulaval.glo4003.ws.domain.user;

import ca.ulaval.glo4003.ws.api.user.exception.EmailAlreadyInUseException;
import ca.ulaval.glo4003.ws.domain.auth.Session;
import ca.ulaval.glo4003.ws.domain.auth.SessionAdministrator;
import ca.ulaval.glo4003.ws.domain.auth.exception.InvalidCredentialsException;
import ca.ulaval.glo4003.ws.domain.user.exception.LoginFailedException;

public class UserService {
  private final UserRepository userRepository;
  private final SessionAdministrator sessionAdministrator;

  public UserService(UserRepository userRepository, SessionAdministrator sessionAdministrator) {

    this.userRepository = userRepository;
    this.sessionAdministrator = sessionAdministrator;
  }

  public void registerUser(User user) {
    if (userRepository.doesUserExist(user.getEmail())) {
      throw new EmailAlreadyInUseException();
    }
    userRepository.registerUser(user);
  }

  public Session login(String email, String password) {
    try {
      return sessionAdministrator.login(email, password);
    } catch (InvalidCredentialsException ignored) {
      throw new LoginFailedException();
    }
  }
}
