package ca.ulaval.glo4003.ws.api.handler;

import ca.ulaval.glo4003.ws.api.handler.exception.UnauthorizedUserException;
import ca.ulaval.glo4003.ws.api.shared.TokenExtractor;
import ca.ulaval.glo4003.ws.context.ServiceLocator;
import ca.ulaval.glo4003.ws.domain.auth.Session;
import ca.ulaval.glo4003.ws.domain.auth.SessionRepository;
import ca.ulaval.glo4003.ws.domain.auth.SessionToken;
import ca.ulaval.glo4003.ws.domain.auth.SessionTokenGenerator;
import ca.ulaval.glo4003.ws.domain.user.Role;
import ca.ulaval.glo4003.ws.domain.user.User;
import ca.ulaval.glo4003.ws.domain.user.UserRepository;
import ca.ulaval.glo4003.ws.infrastructure.exception.SessionDoesNotExistException;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.HttpHeaders;
import java.util.List;

public class RoleHandler {
  private static final ServiceLocator serviceLocator = ServiceLocator.getInstance();

  private final UserRepository userRepository;
  private final SessionRepository sessionRepository;
  private final SessionTokenGenerator tokenGenerator;
  private final TokenExtractor tokenExtractor;

  public RoleHandler() {
    this(
        serviceLocator.resolve(UserRepository.class),
        serviceLocator.resolve(SessionRepository.class),
        serviceLocator.resolve(SessionTokenGenerator.class),
        serviceLocator.resolve(TokenExtractor.class));
  }

  public RoleHandler(
      UserRepository userRepository,
      SessionRepository sessionRepository,
      SessionTokenGenerator tokenGenerator,
      TokenExtractor tokenExtractor) {
    this.userRepository = userRepository;
    this.sessionRepository = sessionRepository;
    this.tokenGenerator = tokenGenerator;
    this.tokenExtractor = tokenExtractor;
  }

  public Session retrieveSession(
      ContainerRequestContext requestContext, List<Role> requestedRoles) {
    String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
    String tokenValue = tokenExtractor.extract(authorizationHeader);
    SessionToken sessionToken = tokenGenerator.generate(tokenValue);
    if (!sessionRepository.doesSessionExist(sessionToken)) {
      throw new SessionDoesNotExistException();
    }
    Session session = sessionRepository.find(sessionToken);
    User user = userRepository.findUser(session.getEmail());

    if (!user.isAllowed(requestedRoles)) {
      throw new UnauthorizedUserException();
    }

    return session;
  }
}
