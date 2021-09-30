package ca.ulaval.glo4003.ws.api.validator;

import ca.ulaval.glo4003.ws.api.util.TokenExtractor;
import ca.ulaval.glo4003.ws.api.validator.exception.UnauthorizedUserException;
import ca.ulaval.glo4003.ws.domain.auth.Session;
import ca.ulaval.glo4003.ws.domain.auth.SessionRepository;
import ca.ulaval.glo4003.ws.domain.auth.SessionTokenGenerator;
import ca.ulaval.glo4003.ws.domain.user.Role;
import ca.ulaval.glo4003.ws.domain.user.User;
import ca.ulaval.glo4003.ws.domain.user.UserRepository;
import ca.ulaval.glo4003.ws.infrastructure.exception.SessionDoesNotExistException;
import ca.ulaval.glo4003.ws.infrastructure.exception.UserNotFoundException;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.HttpHeaders;
import java.util.List;
import java.util.Optional;

public class RoleValidator {
  private final UserRepository userRepository;
  private final SessionRepository sessionRepository;
  private final SessionTokenGenerator tokenGenerator;
  private final TokenExtractor tokenExtractor;

  public RoleValidator(
      UserRepository userRepository,
      SessionRepository sessionRepository,
      SessionTokenGenerator tokenGenerator,
      TokenExtractor tokenExtractor) {
    this.userRepository = userRepository;
    this.sessionRepository = sessionRepository;
    this.tokenGenerator = tokenGenerator;
    this.tokenExtractor = tokenExtractor;
  }

  public void validate(ContainerRequestContext requestContext, List<Role> requestedRoles) {
    String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
    String tokenValue = tokenExtractor.extract(authorizationHeader);
    Optional<Session> optionalSession = sessionRepository.find(tokenGenerator.generate(tokenValue));

    if (optionalSession.isEmpty()) {
      throw new SessionDoesNotExistException();
    }

    Session session = optionalSession.get();

    Optional<User> user = userRepository.findUser(session.getEmail());

    if (user.isEmpty()) {
      throw new UserNotFoundException();
    }

    if (!user.get().isAllowed(requestedRoles)) {
      throw new UnauthorizedUserException();
    }
  }
}
