package ca.ulaval.glo4003.ws.domain.auth;

import ca.ulaval.glo4003.ws.api.handler.exception.UnauthorizedUserException;
import ca.ulaval.glo4003.ws.api.user.exception.EmailAlreadyInUseException;
import ca.ulaval.glo4003.ws.context.ServiceLocator;
import ca.ulaval.glo4003.ws.domain.user.Role;
import ca.ulaval.glo4003.ws.domain.user.User;
import ca.ulaval.glo4003.ws.domain.user.UserRepository;
import ca.ulaval.glo4003.ws.domain.user.credentials.PasswordAdministrator;
import ca.ulaval.glo4003.ws.domain.user.exception.LoginFailedException;
import ca.ulaval.glo4003.ws.infrastructure.exception.SessionDoesNotExistException;
import ca.ulaval.glo4003.ws.service.user.dto.TokenDto;

import java.util.List;

public class SessionAdministrator {
  private static final ServiceLocator serviceLocator = ServiceLocator.getInstance();

  private final UserRepository userRepository;
  private final SessionRepository sessionRepository;
  private final SessionFactory sessionFactory;
  private final PasswordAdministrator passwordAdministrator;
  private final SessionTokenGenerator sessionTokenGenerator;

  public SessionAdministrator() {
    this(
        serviceLocator.resolve(UserRepository.class),
        serviceLocator.resolve(SessionRepository.class),
        serviceLocator.resolve(SessionFactory.class),
        serviceLocator.resolve(PasswordAdministrator.class),
            serviceLocator.resolve(SessionTokenGenerator.class));
  }

  public SessionAdministrator(
      UserRepository userRepository,
      SessionRepository sessionRepository,
      SessionFactory sessionFactory,
      PasswordAdministrator passwordAdministrator,
      SessionTokenGenerator sessionTokenGenerator) {
    this.userRepository = userRepository;
    this.sessionRepository = sessionRepository;
    this.sessionFactory = sessionFactory;
    this.passwordAdministrator = passwordAdministrator;
    this.sessionTokenGenerator = sessionTokenGenerator;
  }

  public Session login(String email, String password) {
    if (!userRepository.doesUserExist(email)
        || !passwordAdministrator.areCredentialsValid(email, password)) {
      throw new LoginFailedException();
    }
    return generateSession(email);
  }

  public void registerUser(User user, String password) {
    if (userRepository.doesUserExist(user.getEmail())) {
      throw new EmailAlreadyInUseException();
    }
    userRepository.registerUser(user);
    passwordAdministrator.register(user.getEmail(), password);
  }

  public boolean isSessionValid(SessionToken token) {
    return sessionRepository.doesSessionExist(token);
  }

  private Session generateSession(String email) {
    Session session = sessionFactory.create(email);
    sessionRepository.save(session);
    return session;
  }

  public void validatePermissions(TokenDto tokenDto, List<Role> requestedRoles) {
    Session session = retrieveSession(tokenDto);
    User user = userRepository.findUser(session.getEmail());

    if (!user.isAllowed(requestedRoles)) {
      throw new UnauthorizedUserException();
    }
  }

  public Session retrieveSession(TokenDto tokenDto) {
    SessionToken sessionToken = sessionTokenGenerator.generate(tokenDto.getToken());
    if (!sessionRepository.doesSessionExist(sessionToken)) {
      throw new SessionDoesNotExistException();
    }
    return sessionRepository.find(sessionToken);
  }

}
