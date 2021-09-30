package ca.ulaval.glo4003.ws.api.filters.secured;

import ca.ulaval.glo4003.ws.api.util.TokenExtractor;
import ca.ulaval.glo4003.ws.domain.auth.Session;
import ca.ulaval.glo4003.ws.domain.auth.SessionAdministrator;
import ca.ulaval.glo4003.ws.domain.auth.SessionFactory;
import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.ext.Provider;
import java.io.IOException;

@Secured
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {

  private String authorizationHeaderName;
  private SessionFactory sessionFactory;
  private SessionAdministrator sessionAdministrator;
  private TokenExtractor tokenExtractor;

  private AuthenticationFilter() {}

  public AuthenticationFilter(
      String authorizationHeaderName,
      SessionFactory sessionFactory,
      SessionAdministrator sessionAdministrator,
      TokenExtractor tokenExtractor) {
    this.authorizationHeaderName = authorizationHeaderName;
    this.sessionFactory = sessionFactory;
    this.sessionAdministrator = sessionAdministrator;
    this.tokenExtractor = tokenExtractor;
  }

  @Override
  public void filter(ContainerRequestContext requestContext) throws IOException {
    String authorizationHeader = requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);

    if (isHeaderInvalid(authorizationHeader)) {
      abortWithUnauthorized(requestContext);
      return;
    }

    validateToken(requestContext, authorizationHeader);
  }

  private boolean isHeaderInvalid(String authorizationHeader) {
    return authorizationHeader == null || !authorizationHeader.startsWith(authorizationHeaderName);
  }

  private void validateToken(ContainerRequestContext requestContext, String authorizationHeader) {
    String tokenValue = tokenExtractor.extract(authorizationHeader);
    Session session = sessionFactory.create(tokenValue);

    if (!sessionAdministrator.isSessionValid(session)) {
      abortWithUnauthorized(requestContext);
    }
  }

  private void abortWithUnauthorized(ContainerRequestContext requestContext) {
    requestContext.abortWith(Response.status(Status.UNAUTHORIZED).build());
  }
}