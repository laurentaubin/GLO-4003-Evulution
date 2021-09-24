package ca.ulaval.glo4003.ws.api.filters;

import ca.ulaval.glo4003.ws.domain.auth.LoginToken;
import ca.ulaval.glo4003.ws.domain.auth.LoginTokenAdministrator;
import ca.ulaval.glo4003.ws.domain.auth.LoginTokenFactory;
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

  private final String authorizationHeaderName;
  private final LoginTokenFactory loginTokenFactory;
  private final LoginTokenAdministrator loginTokenAdministrator;

  public AuthenticationFilter(
      String authorizationHeaderName,
      LoginTokenFactory loginTokenFactory,
      LoginTokenAdministrator loginTokenAdministrator) {
    this.authorizationHeaderName = authorizationHeaderName;
    this.loginTokenFactory = loginTokenFactory;
    this.loginTokenAdministrator = loginTokenAdministrator;
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
    String tokenValue = extractTokenFromHeader(authorizationHeader);
    LoginToken loginToken = loginTokenFactory.create(tokenValue);

    if (!loginTokenAdministrator.isTokenValid(loginToken)) {
      abortWithUnauthorized(requestContext);
    }
  }

  private String extractTokenFromHeader(String authorizationHeader) {
    return authorizationHeader.substring(authorizationHeaderName.length()).trim();
  }

  private void abortWithUnauthorized(ContainerRequestContext requestContext) {
    requestContext.abortWith(Response.status(Status.UNAUTHORIZED).build());
  }
}
