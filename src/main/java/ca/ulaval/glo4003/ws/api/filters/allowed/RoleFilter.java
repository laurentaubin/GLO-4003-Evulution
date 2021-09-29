package ca.ulaval.glo4003.ws.api.filters.allowed;

import ca.ulaval.glo4003.ws.domain.auth.Session;
import ca.ulaval.glo4003.ws.domain.auth.SessionRepository;
import ca.ulaval.glo4003.ws.domain.user.Role;
import ca.ulaval.glo4003.ws.domain.user.User;
import ca.ulaval.glo4003.ws.domain.user.UserRepository;
import ca.ulaval.glo4003.ws.infrastructure.exception.SessionDoesNotExistException;
import ca.ulaval.glo4003.ws.infrastructure.exception.UserNotFoundException;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// TODO Remove from jacoco exclude if we decide to use it
@Allowed
public class RoleFilter implements ContainerRequestFilter {
  private UserRepository userRepository;
  private SessionRepository sessionRepository;
  private String authorizationHeaderName;

  public RoleFilter(
      UserRepository userRepository,
      SessionRepository sessionRepository,
      String authorizationHeaderName) {
    this.userRepository = userRepository;
    this.sessionRepository = sessionRepository;
    this.authorizationHeaderName = authorizationHeaderName;
  }

  @Override
  public void filter(ContainerRequestContext requestContext) {
    String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

    Optional<Session> optionalSession =
        sessionRepository.find(extractTokenFromHeader(authorizationHeader));

    if (optionalSession.isEmpty()) {
      throw new SessionDoesNotExistException();
    }

    Session session = optionalSession.get();

    Optional<User> optionalUser = userRepository.findUser(session.getEmail());

    if (optionalUser.isEmpty()) {
      throw new UserNotFoundException();
    }

    User user = optionalUser.get();

    List<Role> roles =
        Arrays.stream(Allowed.class.getAnnotation(Allowed.class).roles())
            .map(Role::valueOf)
            .collect(Collectors.toList());

    if (!user.isAllowed(roles)) {
      abortWithUnauthorized(requestContext);
    }
  }

  // TODO extract, do composition
  private String extractTokenFromHeader(String authorizationHeader) {
    return authorizationHeader.substring(authorizationHeaderName.length()).trim();
  }

  private void abortWithUnauthorized(ContainerRequestContext requestContext) {
    requestContext.abortWith(Response.status(Status.UNAUTHORIZED).build());
  }
}
