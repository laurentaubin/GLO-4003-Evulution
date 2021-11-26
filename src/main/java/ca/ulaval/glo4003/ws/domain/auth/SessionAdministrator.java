package ca.ulaval.glo4003.ws.domain.auth;

import ca.ulaval.glo4003.ws.domain.auth.exception.InvalidCredentialsException;
import ca.ulaval.glo4003.ws.domain.user.UserFinder;
import ca.ulaval.glo4003.ws.domain.user.UserRepository;

public class SessionAdministrator {

  private final UserFinder userFinder;
  private final SessionRepository sessionRepository;
  private final SessionFactory sessionFactory;

  public SessionAdministrator(
      UserFinder userFinder,
      SessionRepository sessionRepository,
      SessionFactory sessionFactory) {
    this.userFinder = userFinder;
    this.sessionRepository = sessionRepository;
    this.sessionFactory = sessionFactory;
  }

  public Session login(String email, String password) {
    if (!userFinder.doesUserExist(email) || !doesPasswordMatch(email, password)) {
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

  private boolean doesPasswordMatch(String email, String password) {
    return userFinder.findUser(email).getPassword().equals(password);
  }
}
