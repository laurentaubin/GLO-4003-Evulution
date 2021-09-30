package ca.ulaval.glo4003.ws.domain.auth;

import ca.ulaval.glo4003.ws.domain.exception.InvalidCredentialsException;
import ca.ulaval.glo4003.ws.domain.user.User;
import ca.ulaval.glo4003.ws.domain.user.UserRepository;
import java.util.Optional;

public class SessionAdministrator {

  private final UserRepository userRepository;
  private final SessionRepository sessionRepository;
  private final SessionFactory sessionFactory;

  public SessionAdministrator(
      UserRepository userRepository,
      SessionRepository sessionRepository,
      SessionFactory sessionFactory) {
    this.userRepository = userRepository;
    this.sessionRepository = sessionRepository;
    this.sessionFactory = sessionFactory;
  }

  public Session login(String email, String password) {
    Optional<User> user = userRepository.findUser(email);

    if (user.isEmpty()) {
      throw new InvalidCredentialsException();
    }

    if (!user.get().getPassword().equals(password)) {
      throw new InvalidCredentialsException();
    }

    return generateToken(email);
  }

  public boolean isSessionValid(SessionToken token) {
    return sessionRepository.doesSessionExist(token);
  }

  private Session generateToken(String email) {
    Session session = sessionFactory.create(email);

    sessionRepository.save(session);

    return session;
  }
}
