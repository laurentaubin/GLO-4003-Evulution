package ca.ulaval.glo4003.ws.api.filter.secured;

import ca.ulaval.glo4003.ws.api.shared.TokenExtractor;
import ca.ulaval.glo4003.ws.domain.auth.SessionAdministrator;
import ca.ulaval.glo4003.ws.domain.auth.SessionToken;
import ca.ulaval.glo4003.ws.domain.auth.SessionTokenGenerator;
import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.ext.Provider;

@Secured
@Provider
@Priority(Priorities.AUTHENTICATION)
public class AuthenticationFilter implements ContainerRequestFilter {

  private String authorizationHeaderName;
  private SessionAdministrator sessionAdministrator;
  private SessionTokenGenerator sessionTokenGenerator;
  private TokenExtractor tokenExtractor;

  private AuthenticationFilter() {}

  public AuthenticationFilter(
      String authorizationHeaderName,
      SessionAdministrator sessionAdministrator,
      SessionTokenGenerator sessionTokenGenerator,
      TokenExtractor tokenExtractor) {
    this.authorizationHeaderName = authorizationHeaderName;
    this.sessionAdministrator = sessionAdministrator;
    this.sessionTokenGenerator = sessionTokenGenerator;
    this.tokenExtractor = tokenExtractor;
  }

  @Override
  public void filter(ContainerRequestContext requestContext) {
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
    SessionToken sessionToken = sessionTokenGenerator.generate(tokenValue);

    if (!sessionAdministrator.isSessionValid(sessionToken)) {
      abortWithUnauthorized(requestContext);
    }
  }

  private void abortWithUnauthorized(ContainerRequestContext requestContext) {
    requestContext.abortWith(Response.status(Status.UNAUTHORIZED).build());
  }
}
